package net.opengis.swe.x20;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://www.opengis.net/swe/2.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {
	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(name="DataArray")
	private DataArray dataArray;
	
	@XmlElement(name="DataChoice")
	private DataChoice dataChoice;
	
	@XmlElement(name="Quantity")
	private Quantity quantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public DataArray getDataArray() {
		return dataArray;
	}

	public void setDataArray(DataArray dataArray) {
		this.dataArray = dataArray;
	}

	public DataChoice getDataChoice() {
		return dataChoice;
	}

	public void setDataChoice(DataChoice dataChoice) {
		this.dataChoice = dataChoice;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
	

}
