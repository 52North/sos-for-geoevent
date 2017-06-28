package net.opengis.gml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://www.opengis.net/gml")
@XmlAccessorType(XmlAccessType.FIELD)
public class Member {

	@XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
	private String member;

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

}
