
package org.n52.sensorweb.sos.transport;

import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.opengis.om.x10.ObservationCollection;

/**
 * Class for deserializing XML data from a SOS GetObservation response.
 * 
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class ObservationUnmarshaller {

	private final JAXBContext jc;
	private final Unmarshaller unmarshaller;

	public ObservationUnmarshaller() throws JAXBException {
		this.jc = JAXBContext.newInstance(ObservationCollection.class);
		this.unmarshaller = jc.createUnmarshaller();
	}

	/**
	 * Deserializes an InputStream that contains XML data from a SOS
	 * GetObservation response.
	 * 
	 * @param sensorDataInputStream contains the XML data
	 * @return ObservationCollection that contains data from a SOS
	 *         GetObservation response
	 * @throws JAXBException
	 */
	public ObservationCollection readObservationCollection(InputStream sensorDataInputStream) throws JAXBException {
		return (ObservationCollection) unmarshaller.unmarshal(sensorDataInputStream);
	}

}
