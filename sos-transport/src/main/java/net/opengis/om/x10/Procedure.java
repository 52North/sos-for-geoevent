package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Procedure {
	
	@XmlAttribute(name="href", namespace="http://www.w3.org/1999/xlink")
	private String procedure;

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

}
