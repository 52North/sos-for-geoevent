package net.opengis.swe.x20;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://www.opengis.net/swe/2.0")
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
