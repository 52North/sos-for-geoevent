package org.n52.sensorweb.sos.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.MessagingException;

import net.opengis.gml.FeatureMember;
import net.opengis.om.x10.FeatureOfInterest;
import net.opengis.om.x10.Procedure;
import net.opengis.om.x10.Result;

/**
 * Class for building GeoEvents from O&M response values
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class GeoEventBuilder {

	private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private GeoEventCreator geoEventCreator;
	private GeoEventDefinition geoeventDefinition;

	public GeoEventBuilder(GeoEventCreator creator, GeoEventDefinition definition) {
		this.geoEventCreator = creator;
		this.geoeventDefinition = definition;
	}

	/**
	 * Builds a GeoEvent from the specified O&M values
	 * 
	 * @param procedure
	 *            O&M procedure
	 * @param feature
	 *            O&M feature
	 * @param result
	 *            O&M result
	 * @param values
	 *            O&M values
	 * @return GeoEvent built from the O&M values
	 * @throws MessagingException
	 * @throws FieldException
	 * @throws ParseException
	 */
	public GeoEvent buildGeoEvent(Procedure procedure, FeatureOfInterest feature, Result result, String values)
			throws MessagingException, FieldException, ParseException {
		String procedureValue = procedure.getProcedure();

		GeoEvent sensorEvent = geoEventCreator.create(geoeventDefinition.getGuid());
		sensorEvent.setField(0, procedureValue);

		FieldGroup featureGrp = createFeatureOfInterestFieldGroup(feature, sensorEvent);
		sensorEvent.setField(1, featureGrp);

		FieldGroup resultGrp = createResultFieldGroup(result, values, sensorEvent);
		sensorEvent.setField(2, resultGrp);
		return sensorEvent;
	}

	/**
	 * Creates a field group that represents an O&M result record set.
	 * 
	 * @param result
	 *            O&M result
	 * @param values
	 *            O&M values
	 * @param sensorEvent
	 *            GeoEvent that represents the sensor event.
	 * @return FieldGroup that represents an O&M result record set
	 * @throws FieldException
	 * @throws ParseException
	 */
	private FieldGroup createResultFieldGroup(Result result, String values, GeoEvent sensorEvent)
			throws FieldException, ParseException {
		String singleValues[] = values.split(",");
		String valueTime = singleValues[0];
		String valueFeature = singleValues[1];
		String value = singleValues[2];

		String propertyDefinition = result.getDataArray().getElementType().getDataRecord().getFieldList().get(2)
				.getQuantity().getDefinition();
		String propertyUom = result.getDataArray().getElementType().getDataRecord().getFieldList().get(2).getQuantity()
				.getUom().getCode();

		FieldGroup resultGrp = sensorEvent.createFieldGroup("result");
		Date date = null;

		date = (Date) FORMATTER.parse(valueTime);

		resultGrp.setField(0, date);
		resultGrp.setField(1, valueFeature);
		FieldGroup observedPropertyGrp = resultGrp.createFieldGroup("observedProperty");

		observedPropertyGrp.setField(0, propertyDefinition);
		observedPropertyGrp.setField(1, propertyUom);
		observedPropertyGrp.setField(2, Float.parseFloat(value));
		resultGrp.setField(2, observedPropertyGrp);
		return resultGrp;
	}

	/**
	 * Creates a field group that represents the FeatureOfInterest record set
	 * 
	 * @param feature
	 *            O&M feature
	 * @param sensorEvent
	 *            GeoEvent that represents the sensor event.
	 * @return FieldGroup that represents the FeatureOfInterest record set
	 * @throws FieldException
	 */
	private FieldGroup createFeatureOfInterestFieldGroup(FeatureOfInterest feature, GeoEvent sensorEvent)
			throws FieldException {

		FeatureMember member = feature.getFeatureCollection().getFeatureMember();
		String featureId = member.getSamplingPoint().getId();
		String featureDescription = member.getSamplingPoint().getDescription();
		String featureName = member.getSamplingPoint().getName();
		String featurePos = member.getSamplingPoint().getPosition().getPoint().getPos();
		String featurePosCoords[] = featurePos.split(" ");
		Double featurePosX = Double.parseDouble(featurePosCoords[1]);
		Double featurePosY = Double.parseDouble(featurePosCoords[0]);
		Point pt = new Point(featurePosX, featurePosY);
		MapGeometry geom = new MapGeometry(pt, SpatialReference.create(31466));

		FieldGroup featureGrp = sensorEvent.createFieldGroup("featureOfInterest");
		featureGrp.setField(0, featureId);
		featureGrp.setField(1, featureDescription);
		featureGrp.setField(2, featureName);
		featureGrp.setField(3, geom);
		return featureGrp;
	}
}
