package org.n52.sensorweb.sos.adapter;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import net.opengis.om.x10.FeatureOfInterest;
import net.opengis.om.x10.ObservationCollection;
import net.opengis.om.x10.ObservedProperty;
import net.opengis.om.x10.Procedure;
import net.opengis.om.x10.Result;
import net.opengis.swe.x20.DataRecord;

public class ObservationUnmarshallerTest {

	@Test
	public void testParsing() throws JAXBException {
		InputStream is = getClass().getResourceAsStream("test-obs.xml");

		ObservationUnmarshaller unm = new ObservationUnmarshaller();
		ObservationCollection coll = unm.readObservationCollection(is);
		ObservationReader reader = new ObservationReader();

		Assert.assertThat(coll.getMember(), CoreMatchers.notNullValue());
		Assert.assertThat(coll.getMember().getObservation(), CoreMatchers.notNullValue());

		Procedure procedure = coll.getMember().getObservation().getProcedure();
		Assert.assertThat(reader.getProcedure(procedure), CoreMatchers.equalTo("urn:ioos:station:us.glos:45161"));

		FeatureOfInterest feature = coll.getMember().getObservation().getFeatureOfInterest();
		Assert.assertThat(reader.getFeatureId(feature), CoreMatchers.equalTo("urn:ioos:station:us.glos:45161"));
		Assert.assertThat(reader.getFeatureName(feature), CoreMatchers.equalTo("urn:ioos:station:us.glos:45161"));
		Assert.assertThat(reader.getFeatureDescription(feature), CoreMatchers.equalTo(""));

		Assert.assertThat(reader.getFeaturePosX(feature), CoreMatchers.equalTo(-86.336));
		Assert.assertThat(reader.getFeaturePosY(feature), CoreMatchers.equalTo(43.179));

		ObservedProperty obsProperty = coll.getMember().getObservation().getObservedProperty();
		Assert.assertThat(obsProperty.getCompositePhenomenon().getComponents().get(0).getObservedProperty(),
				CoreMatchers.equalTo("http://mmisw.org/ont/cf/parameter/air_temperature"));

		Result result = coll.getMember().getObservation().getResult();

		Assert.assertThat(reader.getPropertyDefinition(result),
				CoreMatchers.equalTo("http://mmisw.org/ont/cf/parameter/air_temperature"));

		Assert.assertThat(reader.getPropertyUom(result), CoreMatchers.equalTo("urn:ogc:def:uom:udunits:2:Cel"));

		String values[] = reader.getResultValues(result);
		Assert.assertThat(values.length, CoreMatchers.equalTo(71));
		Assert.assertThat(values[0],
				CoreMatchers.equalTo("2017-03-09T00:00:00.000Z,us.glos_45161_air_temperature,21.28"));
		Assert.assertThat(values[values.length - 1],
				CoreMatchers.equalTo("2017-03-09T23:20:00.000Z,us.glos_45161_air_temperature,20.45"));

	}

}
