package org.n52.sensorweb.sos.transport;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.transport.Transport;
import com.esri.ges.transport.TransportServiceBase;
import com.esri.ges.transport.util.XmlTransportDefinition;

/**
 * This class is used to create a new SOSInboundTransport by passing a
 * XMLTransportDefinition.
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class SOSInboundTransportService extends TransportServiceBase {
	public SOSInboundTransportService() {
		definition = new XmlTransportDefinition(getResourceAsStream("sos-inbound-transport-definition.xml"));
	}

	/**
	 * Creates a new SOSInboundTransport.
	 */
	public Transport createTransport() throws ComponentException {
		return new SOSInboundTransport(definition);
	}
}