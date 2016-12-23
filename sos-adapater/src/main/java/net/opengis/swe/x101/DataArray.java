package net.opengis.swe.x101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlType(namespace="http://www.opengis.net/swe/1.0.1")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataArray {
	
	@XmlElement(name="elementType")
	private ElementType elementType;

	public ElementType getElementType() {
		return elementType;
	}

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}

	@XmlElement(name="values")
	private String values;

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}
	
}
