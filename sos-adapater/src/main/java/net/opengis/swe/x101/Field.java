package net.opengis.swe.x101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://www.opengis.net/swe/1.0.1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {

	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(name="Quantity")
	private Quantity quantity;
	
	@XmlElement(name="Time")
	private Time time;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
	
}
