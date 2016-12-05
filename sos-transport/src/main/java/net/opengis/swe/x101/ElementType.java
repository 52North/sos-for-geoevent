package net.opengis.swe.x101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://www.opengis.net/swe/1.0.1")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementType {

	@XmlElement(name="DataRecord")
	private DataRecord dataRecord;

	public DataRecord getDataRecord() {
		return dataRecord;
	}

	public void setDataRecord(DataRecord dataRecord) {
		this.dataRecord = dataRecord;
	}
}
