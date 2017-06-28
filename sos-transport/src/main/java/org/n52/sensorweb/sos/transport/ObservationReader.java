package org.n52.sensorweb.sos.transport;

import net.opengis.om.x10.ObservationCollection;

public class ObservationReader {

	public String getLatestSamplingTime(ObservationCollection collection) {
		return collection.getMember().getObservation().getSamplingTime().getTimePeriod().getEndPosition();
	}

}
