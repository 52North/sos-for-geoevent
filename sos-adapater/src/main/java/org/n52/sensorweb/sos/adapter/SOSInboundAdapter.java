package org.n52.sensorweb.sos.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
			buildGeoEvents();
		}

		private void buildGeoEvents() {
			try {
                                ObservationCollection collection = observationParser.readObservationCollection(sensorDataInputStream);
				Observation observation=collection.getMember().getObservation();
				String procedure=observation.getProcedure().getProcedure();
				
				FeatureMember feature=observation.getFeatureOfInterest().getFeatureCollection().getFeatureMember();
				String featureId=feature.getSamplingPoint().getId();
				String featureDescription=feature.getSamplingPoint().getDescription();
				String featureName=feature.getSamplingPoint().getName();
				String featurePos=feature.getSamplingPoint().getPosition().getPoint().getPos();
				String featurePosCoords[]=featurePos.split(" ");
				Double featurePosX=Double.parseDouble(featurePosCoords[1]);
				Double featurePosY=Double.parseDouble(featurePosCoords[0]);
				Point pt=new Point(featurePosX,featurePosY);
				MapGeometry geom=new MapGeometry(pt, SpatialReference.create(31466));
				
				String valueString=observation.getResult().getDataArray().getValues();
				String values []=valueString.split(";");
				
				AdapterDefinition def=(AdapterDefinition)definition;
				GeoEventDefinition geoDef=def.getGeoEventDefinition("SOS-Definition");
				DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				for (int i=0;i<values.length;i++){
					String singleValues[]=values[i].split(",");
					String valueTime=singleValues[0];
					String valueFeature=singleValues[1];
					String value=singleValues[2];
					
					GeoEvent sensorEvent=geoEventCreator.create(geoDef.getGuid());
					sensorEvent.setField(0, procedure);
					
					FieldGroup featureGrp = sensorEvent.createFieldGroup("featureOfInterest");
					featureGrp.setField(0, featureId);
					featureGrp.setField(1, featureDescription);
					featureGrp.setField(2, featureName);
					featureGrp.setField(3, geom);
					sensorEvent.setField(1, featureGrp);
					
					FieldGroup resultGrp=sensorEvent.createFieldGroup("result");
					Date date=(Date)formatter.parse(valueTime);
					resultGrp.setField(0,date);
					resultGrp.setField(1, valueFeature);
					resultGrp.setField(2, value);
					sensorEvent.setField(2, resultGrp);
					
					geoEventListener.receive(sensorEvent);
				}		
				
			} catch (JAXBException | MessagingException | FieldException | ParseException e) {
				LOGGER.error(e.getMessage());
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
		SensorDataEventBuilder builder = new SensorDataEventBuilder(in);
		Thread thread = new Thread(builder);
		thread.start();
	}

	@Override
	public GeoEvent adapt(ByteBuffer buffer, String channelId) {

		return null;
	}
}
