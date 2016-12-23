package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.gml.FeatureCollection;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureOfInterest {
	
	@XmlElement(name="FeatureCollection", namespace="http://www.opengis.net/gml")
	private FeatureCollection featureCollection;
	
	public FeatureCollection getFeatureCollection(){
		return featureCollection;
	}

	public void setFeatureCollection(FeatureCollection featureCollection) {
		this.featureCollection = featureCollection;
	}

}
