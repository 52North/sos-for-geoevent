package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class Process {
	
	@XmlElement(name="member", namespace="http://www.opengis.net/gml")
	private net.opengis.gml.Member member;

	public net.opengis.gml.Member getMember() {
		return member;
	}

	public void setMember(net.opengis.gml.Member member) {
		this.member = member;
	}

}
