package net.opengis.gml;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.sa.x10.SamplingPoint;


@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureMember {
	
	@XmlElement(name="SamplingPoint",namespace="http://www.opengis.net/sampling/1.0")
	private SamplingPoint samplingPoint;
	
	public SamplingPoint getSamplingPoint(){
		return samplingPoint;
	}

	public void setSamplingPoint(SamplingPoint samplingPoint) {
		this.samplingPoint = samplingPoint;
	}

}
