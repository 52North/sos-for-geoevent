package net.opengis.swe.x101;

import java.util.List;

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
public class DataRecord {

	@XmlElement(name="field")
	private List<Field> fieldList;

	public List<Field> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}
}
