package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.swe.x101.DataArray;


@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

	@XmlElement(name="DataArray", namespace="http://www.opengis.net/swe/1.0.1")
	private DataArray dataArray;

	public DataArray getDataArray() {
		return dataArray;
	}

	public void setDataArray(DataArray dataArray) {
		this.dataArray = dataArray;
	}
}
