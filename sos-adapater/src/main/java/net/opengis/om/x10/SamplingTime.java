package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.gml.TimePeriod;


@XmlAccessorType(XmlAccessType.FIELD)
public class SamplingTime {

	@XmlElement(name="TimePeriod", namespace="http://www.opengis.net/gml")
	private TimePeriod timePeriod;

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}
}
