package org.n52.sensorweb.sos.transport;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import net.opengis.om.x10.ObservationCollection;

/**
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class SOSInboundTransportTest {

	@Test
	public void TestRequest() throws JAXBException{
		
		InputStream is = getClass().getResourceAsStream("test-obs.xml");

		ObservationUnmarshaller unm = new ObservationUnmarshaller();
		ObservationCollection coll = unm.readObservationCollection(is);
		Assert.assertThat(coll.getMember(), CoreMatchers.notNullValue());
		Assert.assertThat(coll.getMember().getObservation().getSamplingTime().getTimePeriod().getEndPosition(),
				CoreMatchers.equalTo("2017-03-09T23:20:00.000Z"));
		
	}
}
