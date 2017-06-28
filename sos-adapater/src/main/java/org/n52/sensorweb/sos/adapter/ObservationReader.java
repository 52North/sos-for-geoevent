package org.n52.sensorweb.sos.adapter;

import net.opengis.om.x10.FeatureOfInterest;
import net.opengis.om.x10.Procedure;
import net.opengis.om.x10.Result;


public class ObservationReader {

	public String getProcedure(Procedure procedure) {
		return procedure.getProcess().getMember().getMember();
	}

	public String getPropertyDefinition(Result result) {
		return result.getDataRecord().getFieldList().get(1).getDataArray().getElementType().getDataRecord()
				.getFieldList().get(1).getDataChoice().getItemList().get(0).getDataRecord().getFieldList().get(0)
				.getQuantity().getDefinition();
	}

	public String getPropertyUom(Result result) {
		return result.getDataRecord().getFieldList().get(1).getDataArray().getElementType().getDataRecord()
				.getFieldList().get(1).getDataChoice().getItemList().get(0).getDataRecord().getFieldList().get(0)
				.getQuantity().getUom().getUom();
	}

	public String getFeatureId(FeatureOfInterest feature) {
		return feature.getFeatureCollection().getLocation().getMultiPoint().getPointMembers().getPoint().getName();
	}

	public String getFeatureName(FeatureOfInterest feature) {
		return feature.getFeatureCollection().getLocation().getMultiPoint().getPointMembers().getPoint().getName();
	}

	public String getFeatureDescription(FeatureOfInterest feature) {
		return "";
	}

	public Double getFeaturePosX(FeatureOfInterest feature) {
		return Double.parseDouble(feature.getFeatureCollection().getLocation().getMultiPoint().getPointMembers()
				.getPoint().getPos().split(" ")[1]);
	}
	
	public Double getFeaturePosY(FeatureOfInterest feature) {
		return Double.parseDouble(feature.getFeatureCollection().getLocation().getMultiPoint().getPointMembers()
				.getPoint().getPos().split(" ")[0]);
	}

	public String[] getResultValues(Result result) {
		return result.getDataRecord().getFieldList().get(1).getDataArray().getValues().split("\n");
	}

}
