package org.n52.sensorweb.sos.adapter;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

public class SOSInboundAdapterService extends AdapterServiceBase {
	public SOSInboundAdapterService() {
		definition = new XmlAdapterDefinition(getResourceAsStream("sos-inbound-adapter-definition.xml"));
	}

	@Override
	public Adapter createAdapter() throws ComponentException {
		return new SOSInboundAdapter(definition);
	}
}