package net.opengis.om.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Observation {

	@XmlElement(name="featureOfInterest")
	private FeatureOfInterest featureOfInterest;
	
	@XmlElement(name="samplingTime")
	private SamplingTime samplingTime;
	
	@XmlElement(name="procedure")
	private Procedure procedure;
	
	@XmlElement(name="observedProperty")
	private ObservedProperty observedProperty;

	@XmlElement(name="result")
	private Result result;
	
	public FeatureOfInterest getFeatureOfInterest() {
		return featureOfInterest;
	}

	public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}
	
	public SamplingTime getSamplingTime() {
		return samplingTime;
	}

	public void setSamplingTime(SamplingTime samplingTime) {
		this.samplingTime = samplingTime;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public ObservedProperty getObservedProperty() {
		return observedProperty;
	}

	public void setObservedProperty(ObservedProperty observedProperty) {
		this.observedProperty = observedProperty;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
