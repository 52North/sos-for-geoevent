package org.n52.sensorweb.sos.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

public class SOSInboundTransport extends InboundTransportBase implements Runnable {
	/**
	 * Initialize the i18n Bundle Logger
	 * 
	 * See {@link BundleLogger} for more info.
	 */
	private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(SOSInboundTransport.class);
	private final String REQUEST_KEY = "request";
	private final String SERVICE_KEY = "service";
	private final String VERSION_KEY = "version";
	private final String OFFERING_KEY = "offering";
	private final String OBSERVED_PROPERTY_KEY = "observedProperty";
	private final String PROCEDER_KEY = "procedure";
	private final String RESPONSE_FORMAT_KEY = "responseFormat";
	private final String EVENT_TIME_KEY = "eventTime";

	private final String REQUEST_VALUE = "GetObservation";
	private final String SERVICE_VALUE = "SOS";
	private final String VERSION_VALUE = "1.0.0";
	private final String RESPONSE_FORMAT_XML = "text/xml;subtype=\"om/1.0.0\"";
	private final ObservationUnmarshaller observationParser;
	private final int EVENT_TIME_OFFSET = 1;

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

	public SOSInboundTransport(TransportDefinition definition) throws ComponentException {
		super(definition);
		try {
			LOGGER.info("Create observation parser");
			observationParser = new ObservationUnmarshaller();
			LOGGER.info("Observation Parser Created");
		} catch (JAXBException e) {
			LOGGER.warn(e.getMessage(), e);
			LOGGER.warn("Ok Fehler");
			throw new ComponentException(e.getMessage());
		}
		eventTimeBegin = null;
	}

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
		performInitialRequest=false;//default
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
			if (eventTimeBegin == null||performInitialRequest==true) {
				eventTimeBegin = DateTime.now().minusDays(nDaysInitialRequest);
				performInitialRequest=false;
			}
			requestURI = createHttpURI();
			while (getRunningState() == RunningState.STARTED) {
				setURIEventTimeParameter();
				httpGet = new HttpGet(requestURI);
				receiveData();
				Thread.sleep(requestInterval);
			}

		} catch (Throwable ex) {
			LOGGER.error(ex.getMessage(), ex);
			setRunningState(RunningState.ERROR);
		}
	}

	public synchronized void stop() {
		setRunningState(RunningState.STOPPING);
		setRunningState(RunningState.STOPPED);
	}

	/**
	 * Receive the raw byte data from the GetObservation request
	 * 
	 * @param httpGet
	 *            HTTP-GET request for the GetObservation request
	 */
	private void receiveData() {
		CloseableHttpResponse response = null;
		ByteBuffer bb = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream inStream = entity.getContent();

			byte[] data = getByteArrayFromInputStream(inStream);

			ObservationCollection collection = getObservationCollection(new ByteArrayInputStream(data));
			if (hasEventValues(collection)) {
				eventTimeBegin = getLatestSamplingTime(collection).plusSeconds(EVENT_TIME_OFFSET);
				bb = ByteBuffer.allocate(data.length);
				bb.put(data);
				bb.flip();
				LOGGER.info("SEND BYTE DATA");
				byteListener.receive(bb, "");
				bb.clear();
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			setRunningState(RunningState.ERROR);
		} catch (BufferOverflowException boe) {
			LOGGER.error("BUFFER_OVERFLOW_ERROR", boe);
			bb.clear();
			setRunningState(RunningState.ERROR);
		} finally {
			try {
				httpclient.close();
				response.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				setRunningState(RunningState.ERROR);
			}
		}
	}

	/**
	 * Creates an array of bytes from an InputStream
	 * 
	 * @param is
	 *            InputStream
	 * @return byte array
	 */
	private byte[] getByteArrayFromInputStream(InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nBytesRead;
		int size = 1024;
		byte[] data = new byte[size];

		try {
			/*
			 * Write data.length bytes repeatedly from the InputStream into a
			 * ByteBuffer until the stream is at the end of file
			 */
			while ((nBytesRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nBytesRead);
			}
			buffer.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			setRunningState(RunningState.ERROR);
		}
		return buffer.toByteArray();
	}

	/**
	 * Creates a HTTP-GET request for the SOS GetObservation request from the
	 * specified URL and parameter properties.
	 * 
	 * @return GetObservation request as HTTP-GET
	 */
	private URI createHttpURI() {
		String urlPrefix = "http://";
		URI uri;
		URI fullUri = null;
		try {
			URIBuilder builder = new URIBuilder();

			// Create a valid URI
			if (url.startsWith(urlPrefix)) {
				uri = new URI(url);
			} else {
				StringBuilder sb = new StringBuilder(url);
				sb.insert(0, urlPrefix);
				uri = new URI(sb.toString());
			}
			// Create the URI for the request with all parameters
			fullUri = builder.setScheme("http").setHost(uri.getHost()).setPath(uri.getPath())
					.setParameter(REQUEST_KEY, REQUEST_VALUE).setParameter(SERVICE_KEY, SERVICE_VALUE)
					.setParameter(VERSION_KEY, VERSION_VALUE).setParameter(OFFERING_KEY, offering)
					.setParameter(OBSERVED_PROPERTY_KEY, observedProperty).setParameter(PROCEDER_KEY, procedure)
					.setParameter(EVENT_TIME_KEY, "").setParameter(RESPONSE_FORMAT_KEY, RESPONSE_FORMAT_XML).build();
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage());
			setRunningState(RunningState.ERROR);
		}
		return fullUri;
	}

	/**
	 * Sets the the event time parameter for the GetObservation request
	 */
	private void setURIEventTimeParameter() {
		URIBuilder builder = new URIBuilder(this.requestURI);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String formattedEventTimeBegin = formatter.print(eventTimeBegin);
		String formmettedEventTimeEnd = formatter.print(DateTime.now());
		String eventTimeValue = new StringBuilder(formattedEventTimeBegin).append("/").append(formmettedEventTimeEnd)
				.toString();
		LOGGER.info("TimeBegin: " + formattedEventTimeBegin + " TimeEnd: " + formmettedEventTimeEnd
				+ " EventTimeValue: " + eventTimeValue);
		try {
			requestURI = builder.setParameter(EVENT_TIME_KEY, eventTimeValue).build();
			LOGGER.info("Request URI: " + requestURI.toString());
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage());
			setRunningState(RunningState.ERROR);
		}
	}

	private boolean hasEventValues(ObservationCollection collection) {
		if (collection.getMember().getObservation() != null)
			return true;
		else
			return false;
	}

	/**
	 * Determines the latest sampling time from the response
	 * 
	 * @param inStream
	 *            InputStream that has to be parsed
	 * @return the latest sampling time
	 */
	private DateTime getLatestSamplingTime(ObservationCollection collection) {
		String latestSamplingTime = collection.getMember().getObservation().getSamplingTime().getTimePeriod()
				.getEndPosition();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateTime date = formatter.parseDateTime(latestSamplingTime);
		return date;
	}

	private ObservationCollection getObservationCollection(InputStream inStream) {
		ObservationCollection collection = null;
		try {
			collection = observationParser.readObservationCollection(inStream);
		} catch (JAXBException e) {

			LOGGER.error(e.getMessage());
		}
		return collection;
	}
}