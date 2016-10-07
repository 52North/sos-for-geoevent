package org.n52.sensorweb.sos.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.component.RunningException;
import com.esri.ges.core.component.RunningState;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.transport.InboundTransportBase;
import com.esri.ges.transport.TransportDefinition;

public class SOSInboundTransport extends InboundTransportBase implements Runnable {
	/**
	 * Initialize the i18n Bundle Logger
	 * 
	 * See {@link BundleLogger} for more info.
	 */
	private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(SOSInboundTransport.class);
	private final int REQUEST_INTERVAL = 10000;
	private final String REQUEST_KEY = "request";
	private final String SERVICE_KEY = "service";
	private final String VERSION_KEY = "version";
	private final String OFFERING_KEY = "offering";
	private final String OBSERVED_PROPERTY_KEY = "observedProperty";
	private final String PROCEDER_KEY = "procedure";
	private final String RESPONSE_FORMAT_KEY = "responseFormat";

	private final String REQUEST_VALUE = "GetObservation";
	private final String SERVICE_VALUE = "SOS";
	private final String VERSION_VALUE = "1.0.0";
	private final String RESPONSE_FORMAT_XML = "text/xml;subtype=\"om/1.0.0\"";

	private String url;
	private String offering;
	private String observedProperty;
	private String procedure;
	// private String featureOfInterest;

	private HttpClient client;

	private Thread thread = null;

	public SOSInboundTransport(TransportDefinition definition) throws ComponentException {
		super(definition);
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

		/*
		 * featureOfInterest = ""; if
		 * (getProperty("featureOfInterest").isValid()) { String value =
		 * (String) getProperty("featureOfInterest").getValue(); if
		 * (!value.trim().equals("")) { featureOfInterest = value; } }
		 */
	}

	public void run() {
		try {
			applyProperties();
			HttpGet httpGet = createHttpGet();
			client = HttpClientBuilder.create().build();
			setRunningState(RunningState.STARTED);
			while (getRunningState() == RunningState.STARTED) {
				receiveData(httpGet);
				Thread.sleep(REQUEST_INTERVAL);
			}			

		} 
		catch(InterruptedException ex){
			thread.interrupt();
		}
		catch (Throwable ex) {
			LOGGER.error(ex.getMessage(), ex);
			setRunningState(RunningState.ERROR);
		}

	}

	/**
	 * Receive the raw byte data from the GetObservation request
	 * @param httpGet HTTP-GET request for the GetObservation request
	 */
	private void receiveData(HttpGet httpGet) {
		// TODO Auto-generated method stub
		HttpResponse response;
		ByteBuffer bb = null;

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream inStream = entity.getContent();

			byte[] data = getByteArrayFromInputStream(inStream);

			bb = ByteBuffer.allocate(data.length);

			bb.put(data);
			bb.flip();
			byteListener.receive(bb, "");
			bb.clear();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setRunningState(RunningState.ERROR);
		} catch (BufferOverflowException boe) {
			LOGGER.error("BUFFER_OVERFLOW_ERROR", boe);
			bb.clear();
			setRunningState(RunningState.ERROR);
		}
	}

	/**
	 * Creates an InputStream into a byte array
	 * 
	 * @param is
	 *            InputStream
	 * @return byte array
	 */
	private byte[] getByteArrayFromInputStream(InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		int size = 1024;
		byte[] data = new byte[size];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	/**
	 * Creates a HTTP-GET request for the SOS GetObservation request from the
	 * specified URL and parameter properties.
	 * 
	 * @return GetObservation request as HTTP-GET
	 */
	private HttpGet createHttpGet() {
		HttpGet httpGet = null;
		try {
			URI uri = new URIBuilder().setScheme("http").setHost(url).setParameter(REQUEST_KEY, REQUEST_VALUE)
					.setParameter(SERVICE_KEY, SERVICE_VALUE).setParameter(VERSION_KEY, VERSION_VALUE)
					.setParameter(OFFERING_KEY, offering).setParameter(OBSERVED_PROPERTY_KEY, observedProperty)
					.setParameter(PROCEDER_KEY, procedure).setParameter(RESPONSE_FORMAT_KEY, RESPONSE_FORMAT_XML)
					.build();
			httpGet = new HttpGet(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpGet;
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

	public synchronized void stop() {
		if (getRunningState()==RunningState.STARTED)
		{
			setRunningState(RunningState.STOPPING);
			thread.interrupt();
		}
		setRunningState(RunningState.STOPPED);

	}
}