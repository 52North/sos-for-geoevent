package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlRootElement(name="ObservationCollection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObservationCollection {

	@XmlElement(name="member")
	private Member member;
	
	@XmlAttribute(name="id", namespace="http://www.opengis.net/gml")
	private String id;
	
	
	public Member getMember() {
		return member;
	}
	
	public void setMember(Member member){
		this.member=member;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
