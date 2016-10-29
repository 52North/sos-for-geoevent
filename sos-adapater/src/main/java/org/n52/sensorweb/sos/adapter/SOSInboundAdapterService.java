package org.n52.sensorweb.sos.adapter;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.core.component.ComponentException;

public class SOSInboundAdapterService extends AdapterServiceBase
{
	public SOSInboundAdapterService()
	{
	  definition = new SOSInboundAdapterDefinition();
	}
	
	@Override
	public Adapter createAdapter() throws ComponentException
	{
	  return new SOSInboundAdapter(definition);
	}
}