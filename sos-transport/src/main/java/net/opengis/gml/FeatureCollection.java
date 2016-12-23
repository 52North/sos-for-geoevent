package net.opengis.gml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlType(namespace="http://www.opengis.net/gml")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureCollection {

	@XmlElement(name="featureMember")
	private FeatureMember featureMember;
		
	public FeatureMember getFeatureMember(){
		return featureMember;
	}
	
	public void setFeatureMember(FeatureMember featureMember) {
		this.featureMember = featureMember;
	}

}
