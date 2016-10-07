package org.n52.sensorweb.sos.transport;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.transport.Transport;
import com.esri.ges.transport.TransportServiceBase;
import com.esri.ges.transport.util.XmlTransportDefinition;

public class SOSInboundTransportService extends TransportServiceBase
{
  public SOSInboundTransportService()
  {
    definition = new XmlTransportDefinition(getResourceAsStream("sos-inbound-transport-definition.xml"));
  }
  
  public Transport createTransport() throws ComponentException
  {
    return new SOSInboundTransport(definition);
  }
}