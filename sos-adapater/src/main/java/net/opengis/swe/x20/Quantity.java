package net.opengis.swe.x20;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://www.opengis.net/swe/2.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class Quantity {
	@XmlAttribute(name="definition")
	private String definition;
	
	@XmlElement(name="uom")
	private Uom uom;

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public Uom getUom() {
		return uom;
	}

	public void setUom(Uom uom) {
		this.uom = uom;
	}
}
