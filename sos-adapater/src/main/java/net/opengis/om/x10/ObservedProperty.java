package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.swe.x101.CompositePhenomenon;

@XmlAccessorType(XmlAccessType.FIELD)
public class ObservedProperty {
	
	@XmlElement(name="CompositePhenomenon", namespace="http://www.opengis.net/swe/1.0.1")
	private CompositePhenomenon compositePhenomenon;

	public CompositePhenomenon getCompositePhenomenon() {
		return compositePhenomenon;
	}

	public void setCompositePhenomenon(CompositePhenomenon compositePhenomenon) {
		this.compositePhenomenon = compositePhenomenon;
	}

}
