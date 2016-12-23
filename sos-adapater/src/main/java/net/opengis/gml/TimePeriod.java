package net.opengis.gml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlType(namespace="http://www.opengis.net/gml")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimePeriod {

	@XmlElement(name="beginPosition")
	private String beginPosition;
	
	@XmlElement(name="endPosition")
	private String endPosition;

	public String getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(String beginPosition) {
		this.beginPosition = beginPosition;
	}

	public String getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(String endPosition) {
		this.endPosition = endPosition;
	}
	
	
}
