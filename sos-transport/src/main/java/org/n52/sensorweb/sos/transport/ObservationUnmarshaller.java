
package org.n52.sensorweb.sos.transport;

import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.opengis.om.x10.ObservationCollection;

/**
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

    public ObservationCollection readObservationCollection(InputStream sensorDataInputStream) throws JAXBException {
        return (ObservationCollection) unmarshaller.unmarshal(sensorDataInputStream);
    }

}
