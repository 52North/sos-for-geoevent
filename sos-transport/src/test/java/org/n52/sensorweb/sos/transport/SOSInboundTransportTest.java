package org.n52.sensorweb.sos.transport;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import net.opengis.om.x10.ObservationCollection;

public class SOSInboundTransportTest {

	@Test
	public void TestRequest() throws JAXBException{
		
		InputStream is = getClass().getResourceAsStream("test-obs.xml");

		ObservationUnmarshaller unm = new ObservationUnmarshaller();
		ObservationCollection coll = unm.readObservationCollection(is);
		Assert.assertThat(coll.getMember(), CoreMatchers.notNullValue());
		Assert.assertThat(coll.getMember().getObservation().getSamplingTime().getTimePeriod().getEndPosition(),
				CoreMatchers.equalTo("2016-10-27T11:15:00.000Z"));
		
	}
}
