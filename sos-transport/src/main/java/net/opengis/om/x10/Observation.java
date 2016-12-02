package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Observation {
	
	@XmlElement(name="samplingTime")
	private SamplingTime samplingTime;
	
	
	public SamplingTime getSamplingTime() {
		return samplingTime;
	}

	public void setSamplingTime(SamplingTime samplingTime) {
		this.samplingTime = samplingTime;
	}

}
