package net.opengis.gml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MultiPoint {

	@XmlElement(name="pointMembers")
	private PointMembers pointMembers;

	public PointMembers getPointMembers() {
		return pointMembers;
	}

	public void setPointMembers(PointMembers pointMembers) {
		this.pointMembers = pointMembers;
	}
}
