package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.swe.x101.DataArray;
import net.opengis.swe.x20.DataRecord;


/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

	@XmlElement(name="DataRecord", namespace="http://www.opengis.net/swe/2.0")
	private DataRecord dataRecord;

	public DataRecord getDataRecord() {
		return dataRecord;
	}

	public void setDataRecord(DataRecord dataRecord) {
		this.dataRecord = dataRecord;
	}
}
