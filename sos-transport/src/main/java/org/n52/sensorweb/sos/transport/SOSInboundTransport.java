package org.n52.sensorweb.sos.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.HttpGet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.component.RunningException;
import com.esri.ges.core.component.RunningState;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.transport.InboundTransportBase;
import com.esri.ges.transport.TransportDefinition;

import net.opengis.om.x10.ObservationCollection;

/**
 * Transport class for requesting sensor data from the PegelOnline Sensor
 * Observation Service and receiving the data.
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class SOSInboundTransport extends InboundTransportBase implements Runnable {
	/**
	 * Initialize the i18n Bundle Logger
	 * 
	 * See {@link BundleLogger} for more info.
	 */
	private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(SOSInboundTransport.class);

	private final ObservationUnmarshaller observationParser;
	private final DataReceiver dataReceiver;
	private final RequestBuilder requestBuilder;
	private final int EVENT_TIME_OFFSET = 1;
	private final String ERROR_MESSAGE = "Exception occurs while requesting following URL:";

	private String url;
	private String offering;
	private String observedProperty;
	private String procedure;
	private int requestInterval;
	private int nDaysInitialRequest;
	private DateTime eventTimeBegin;
	private URI requestURI;
	private HttpGet httpGet;
	private boolean performInitialRequest;

	private Thread thread = null;

	/**
	 * Creates a new instance of this class from a specified TransportDefinition
	 * 
	 * @param definition
	 *            TransportDefinition
	 * @throws ComponentException
	 */
	public SOSInboundTransport(TransportDefinition definition) throws ComponentException {
		super(definition);
		this.requestBuilder = new RequestBuilder();
		this.dataReceiver = new DataReceiver();
		try {
			observationParser = new ObservationUnmarshaller();
		} catch (JAXBException e) {
			LOGGER.warn(e.getMessage(), e);
			throw new ComponentException(e.getMessage());
		}
		eventTimeBegin = null;
	}

	/**
	 * Obtains the user specified properties.
	 * 
	 * @throws Exception
	 */
	public void applyProperties() throws Exception {
		url = "";
		if (getProperty("url").isValid()) {
			String value = (String) getProperty("url").getValue();
			if (!value.trim().equals("")) {
				url = value;
			}
		}

		offering = "";
		if (getProperty("offering").isValid()) {
			String value = (String) getProperty("offering").getValue();
			if (!value.trim().equals("")) {
				offering = value;
			}
		}

		observedProperty = "";
		if (getProperty("observedProperty").isValid()) {
			String value = (String) getProperty("observedProperty").getValue();
			if (!value.trim().equals("")) {
				observedProperty = value;
			}
		}

		procedure = "";
		if (getProperty("procedure").isValid()) {
			String value = (String) getProperty("procedure").getValue();
			if (!value.trim().equals("")) {
				procedure = value;
			}
		}
		requestInterval = 10000; // default
		if (getProperty("requestInterval").isValid()) {
			int value = (Integer) getProperty("requestInterval").getValue();
			if (value > 0 && value != requestInterval) {
				// requestInterval is in milliseconds
				requestInterval = value * 1000;
			}
		}

		nDaysInitialRequest = 3; // default
		if (getProperty("eventTimeBegin").isValid()) {
			int value = (Integer) getProperty("eventTimeBegin").getValue();
			if (value > 0 && value != nDaysInitialRequest) {
				nDaysInitialRequest = value;
			}
		}
		performInitialRequest = false;// default
		if (getProperty("performInitialRequest").isValid()) {
			boolean value = (boolean) getProperty("performInitialRequest").getValue();
			if (value != performInitialRequest) {
				performInitialRequest = value;
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void start() throws RunningException {
		try {
			switch (getRunningState()) {
			case STARTING:
			case STARTED:
			case STOPPING:
				return;
			}
			setRunningState(RunningState.STARTING);
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			LOGGER.error("UNEXPECTED_ERROR_STARTING", e);
			stop();
		}
	}

	public void run() {
		try {
			applyProperties();
			setRunningState(RunningState.STARTED);

			// Set the begin time for the following request to the DateTime
			// that was the specified n-days before the current DateTime
			// if the initial request flag is true. Otherwise set the
			// begin time to the current DateTime.
			if (performInitialRequest == true) {
				eventTimeBegin = DateTime.now().minusDays(nDaysInitialRequest);
				performInitialRequest = false;
			} else {
				if (eventTimeBegin == null) {
					eventTimeBegin = DateTime.now();
				}
			}

			requestURI = requestBuilder.createHttpURI(url, procedure, offering, observedProperty);

			while (getRunningState() == RunningState.STARTED) {
				requestURI = requestBuilder.setURIEventTimeParameter(requestURI, eventTimeBegin);
				LOGGER.info("Request URI: " + requestURI.toString());
				httpGet = new HttpGet(requestURI);
				byte[] data = dataReceiver.receiveData(httpGet);

				ObservationCollection collection = observationParser
						.readObservationCollection(new ByteArrayInputStream(data));
				if (collection.getMember().getObservation() != null) {
					eventTimeBegin = getLatestSamplingTime(collection).plusSeconds(EVENT_TIME_OFFSET);
					ByteBuffer bb = ByteBuffer.allocate(data.length);
					bb.put(data);
					bb.flip();
					byteListener.receive(bb, "");
					bb.clear();
				}
				Thread.sleep(requestInterval);
			}
		} catch (JAXBException e) {
			LOGGER.warn(e.getMessage() + System.lineSeparator() + ERROR_MESSAGE + System.lineSeparator()
					+ requestURI.toString());
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage() + System.lineSeparator() + ERROR_MESSAGE + System.lineSeparator()
					+ requestURI.toString());
			setRunningState(RunningState.ERROR);
			thread.interrupt();
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + System.lineSeparator() + ERROR_MESSAGE + System.lineSeparator()
					+ requestURI.toString());
			setRunningState(RunningState.ERROR);
		}
	}

	public synchronized void stop() {
		setRunningState(RunningState.STOPPING);
		setRunningState(RunningState.STOPPED);
	}

	/**
	 * Determines the latest sampling time from ObservationCollection.
	 * 
	 * @param collection
	 *            ObservationCollection
	 * @return DateTime that represents the latest sampling time.
	 */
	private DateTime getLatestSamplingTime(ObservationCollection collection) {
		String latestSamplingTime = collection.getMember().getObservation().getSamplingTime().getTimePeriod()
				.getEndPosition();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateTime date = formatter.parseDateTime(latestSamplingTime);
		return date;
	}

}