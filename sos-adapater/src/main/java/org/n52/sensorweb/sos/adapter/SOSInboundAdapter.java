package org.n52.sensorweb.sos.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBException;

import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManagerException;
import com.esri.ges.messaging.MessagingException;

import net.opengis.gml.FeatureMember;
import net.opengis.om.x10.Observation;
import net.opengis.om.x10.ObservationCollection;

public class SOSInboundAdapter extends InboundAdapterBase {
	/**
	 * Initialize the i18n Bundle Logger
	 * 
	 * See {@link BundleLogger} for more info.
	 */
	private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(SOSInboundAdapter.class);
	private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private final ObservationUnmarshaller observationParser;

	public SOSInboundAdapter(AdapterDefinition definition) throws ComponentException {
		super(definition);
		try {
			this.observationParser = new ObservationUnmarshaller();
		} catch (JAXBException ex) {
			LOGGER.warn(ex.getMessage(), ex);
			throw new ComponentException(ex.getMessage());
		}
	}

	private class SensorDataEventBuilder implements Runnable {
		private InputStream sensorDataInputStream;

		public SensorDataEventBuilder(InputStream in) {
			sensorDataInputStream = in;
		}

		@Override
		public void run() {
			createGeoEvents();
		}

		private void createGeoEvents() {
			try {
				//Parse the SOS response and get all necessary values to build the GeoEvents
				ObservationCollection collection = observationParser.readObservationCollection(sensorDataInputStream);
				Observation observation = collection.getMember().getObservation();
				String procedure = observation.getProcedure().getProcedure();

				FeatureMember feature = observation.getFeatureOfInterest().getFeatureCollection().getFeatureMember();
				String featureId = feature.getSamplingPoint().getId();
				String featureDescription = feature.getSamplingPoint().getDescription();
				String featureName = feature.getSamplingPoint().getName();
				String featurePos = feature.getSamplingPoint().getPosition().getPoint().getPos();
				String featurePosCoords[] = featurePos.split(" ");
				Double featurePosX = Double.parseDouble(featurePosCoords[1]);
				Double featurePosY = Double.parseDouble(featurePosCoords[0]);
				Point pt = new Point(featurePosX, featurePosY);
				MapGeometry geom = new MapGeometry(pt, SpatialReference.create(31466));

				String valueString = observation.getResult().getDataArray().getValues();
				String values[] = valueString.split(";");

				AdapterDefinition def = (AdapterDefinition) definition;
				GeoEventDefinition geoDef = def.getGeoEventDefinition("SOS-Definition");
				
				// Check if the GeoEvent definition exists. Otherwise add it to
				// the GeoEventDefinitionManager
				if (geoEventCreator.getGeoEventDefinitionManager().searchGeoEventDefinition(geoDef.getName(),
						geoDef.getOwner()) == null) {
					try {
						geoEventCreator.getGeoEventDefinitionManager().addGeoEventDefinition(geoDef);
					} catch (GeoEventDefinitionManagerException e) {
						// TODO Auto-generated catch block
						LOGGER.error(e.getMessage());
					}
				}

				//Create a GeoEvent for each record set from the O&M result
				for (int i = 0; i < values.length; i++) {
					String valueSet = values[i];
					GeoEvent sensorEvent = buildGeoEvent(procedure, featureId, featureDescription, featureName, geom,
							valueSet, geoDef);

					geoEventListener.receive(sensorEvent);
				}

			} catch (JAXBException | MessagingException | FieldException e) {
				LOGGER.error(e.getMessage());
			}
		}

		/**
		 * Builds a GeoEvent from the specified values
		 * 
		 * @param procedure
		 *            name of the procedure
		 * @param featureId
		 *            FeatureOfInterest ID
		 * @param featureDescription
		 *            FeatureOfInterest description
		 * @param featureName
		 *            FeatureOfInterest name
		 * @param geom
		 *            FeatureOfInterest geometry
		 * @param values
		 *            value record set from the O&M result
		 * @param geoDef
		 *            GeoEvent Definition
		 * @return GeoEvent with the specified attribute values
		 * @throws MessagingException
		 * @throws FieldException
		 */
		public GeoEvent buildGeoEvent(String procedure, String featureId, String featureDescription, String featureName,
				MapGeometry geom, String values, GeoEventDefinition geoDef) throws MessagingException, FieldException {
			String singleValues[] = values.split(",");
			String valueTime = singleValues[0];
			String valueFeature = singleValues[1];
			String value = singleValues[2];

			GeoEvent sensorEvent = geoEventCreator.create(geoDef.getGuid());
			sensorEvent.setField(0, procedure);

			FieldGroup featureGrp = createFeatureOfInterestFieldGroup(featureId, featureDescription, featureName, geom,
					sensorEvent);
			sensorEvent.setField(1, featureGrp);

			FieldGroup resultGrp = createResultFieldGroup(valueTime, valueFeature, value, sensorEvent);
			sensorEvent.setField(2, resultGrp);
			return sensorEvent;
		}

		/**
		 * Creates a field group that represents the result record set
		 * 
		 * @param valueTime
		 * @param valueFeature
		 * @param value
		 * @param sensorEvent
		 * @return
		 * @throws FieldException
		 */
		private FieldGroup createResultFieldGroup(String valueTime, String valueFeature, String value,
				GeoEvent sensorEvent) throws FieldException {
			FieldGroup resultGrp = sensorEvent.createFieldGroup("result");
			Date date = null;
			try {
				date = (Date) FORMATTER.parse(valueTime);
			} catch (ParseException e) {
				LOGGER.error(e.getMessage());
			}
			resultGrp.setField(0, date);
			resultGrp.setField(1, valueFeature);
			resultGrp.setField(2, Float.parseFloat(value));
			return resultGrp;
		}

		/**
		 * Creates a field groupt that represents the FeatureOfInterest record
		 * set
		 * 
		 * @param featureId
		 * @param featureDescription
		 * @param featureName
		 * @param geom
		 * @param sensorEvent
		 * @return
		 * @throws FieldException
		 */
		private FieldGroup createFeatureOfInterestFieldGroup(String featureId, String featureDescription,
				String featureName, MapGeometry geom, GeoEvent sensorEvent) throws FieldException {
			FieldGroup featureGrp = sensorEvent.createFieldGroup("featureOfInterest");
			featureGrp.setField(0, featureId);
			featureGrp.setField(1, featureDescription);
			featureGrp.setField(2, featureName);
			featureGrp.setField(3, geom);
			return featureGrp;
		}
	}

	@Override
	public void receive(ByteBuffer buffer, String channelId) {
		if (!buffer.hasRemaining()) {
			return;
		}

		int size = buffer.remaining();
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		SensorDataEventBuilder builder = new SensorDataEventBuilder(in);
		Thread thread = new Thread(builder);
		thread.start();
	}

	@Override
	public GeoEvent adapt(ByteBuffer buffer, String channelId) {

		return null;
	}
}
