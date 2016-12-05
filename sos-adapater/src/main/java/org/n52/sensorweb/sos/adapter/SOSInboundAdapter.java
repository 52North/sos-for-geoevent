package org.n52.sensorweb.sos.adapter;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import javax.xml.bind.JAXBException;


import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManagerException;
import com.esri.ges.messaging.MessagingException;

import net.opengis.om.x10.FeatureOfInterest;
import net.opengis.om.x10.Observation;
import net.opengis.om.x10.ObservationCollection;
import net.opengis.om.x10.Procedure;
import net.opengis.om.x10.Result;

/**
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class SOSInboundAdapter extends InboundAdapterBase {
	/**
	 * Initialize the i18n Bundle Logger
	 * 
	 * See {@link BundleLogger} for more info.
	 */
	private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(SOSInboundAdapter.class);

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
		private GeoEventBuilder builder;
		private ObservationCollection collection;

		public SensorDataEventBuilder(GeoEventBuilder builder, ObservationCollection collection) {
			this.builder = builder;
			this.collection = collection;
		}

		@Override
		public void run() {
			Observation observation = collection.getMember().getObservation();

			Procedure procedure = observation.getProcedure();
			FeatureOfInterest feature = observation.getFeatureOfInterest();
			Result result = observation.getResult();

			String valueString = result.getDataArray().getValues();
			String values[] = valueString.split(";");

			// Create a GeoEvent for each record set from the O&M result
			for (int i = 0; i < values.length; i++) {
				String valueSet = values[i];
				GeoEvent sensorEvent;
				try {
					sensorEvent = this.builder.buildGeoEvent(procedure, feature, result, valueSet);
					geoEventListener.receive(sensorEvent);
				} catch (MessagingException | FieldException | ParseException e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage());
				}
			}
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

		ObservationCollection collection;
		try {
			collection = observationParser.readObservationCollection(in);

			AdapterDefinition def = (AdapterDefinition) definition;
			GeoEventDefinition geoDef = def.getGeoEventDefinition("SOS-Definition");

			// Check if the GeoEvent definition exists. Otherwise add it to
			// the GeoEventDefinitionManager
			if (geoEventCreator.getGeoEventDefinitionManager().searchGeoEventDefinition(geoDef.getName(),
					geoDef.getOwner()) == null) {
				geoEventCreator.getGeoEventDefinitionManager().addGeoEventDefinition(geoDef);
			}

			GeoEventBuilder geoEventBuilder = new GeoEventBuilder(geoEventCreator, geoDef);

			SensorDataEventBuilder builder = new SensorDataEventBuilder(geoEventBuilder, collection);
			Thread thread = new Thread(builder);
			thread.start();
		} catch (JAXBException | GeoEventDefinitionManagerException e) {
			LOGGER.error(e.getMessage());
		}

	}

	@Override
	public GeoEvent adapt(ByteBuffer buffer, String channelId) {

		return null;
	}
}
