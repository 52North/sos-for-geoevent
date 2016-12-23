package org.n52.sensorweb.sos.adapter;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

/**
 * This class creates a new SOSInboundAdapter from a XmlAdapterDefinition.
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class SOSInboundAdapterService extends AdapterServiceBase {
	public SOSInboundAdapterService() {
		definition = new XmlAdapterDefinition(getResourceAsStream("sos-inbound-adapter-definition.xml"));
	}

	@Override
	public Adapter createAdapter() throws ComponentException {
		return new SOSInboundAdapter(definition);
	}
}