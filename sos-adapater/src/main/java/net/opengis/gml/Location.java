package net.opengis.gml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
	@XmlElement(name = "MultiPoint")
	private MultiPoint multiPoint;

	public MultiPoint getMultiPoint() {
		return multiPoint;
	}

	public void setMultiPoint(MultiPoint multiPoint) {
		this.multiPoint = multiPoint;
	}

}
