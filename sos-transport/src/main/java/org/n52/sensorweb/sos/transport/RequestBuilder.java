package org.n52.sensorweb.sos.transport;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This class is used for building a GetObservation request as HTTP-GET
 * 
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
public class RequestBuilder {

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

	/**
	 * Creates a HTTP-GET request for the SOS GetObservation request from the
	 * specified URL and parameter properties.
	 * 
	 * @return GetObservation request as HTTP-GET
	 */
	/**
	 * Creates a URI for the GetObservation HTTP-GET request from the specified
	 * parameters.
	 * 
	 * @param url
	 *            base URL
	 * @param procedure
	 *            parameter that specifies the procedure
	 * @param offering
	 *            parameter that specifies the offering
	 * @param observedProperty
	 *            parameter that specifies the observed property
	 * @return GetObservation HTTP-GET URI
	 * @throws URISyntaxException
	 */
	public URI createHttpURI(String url, String procedure, String offering, String observedProperty)
			throws URISyntaxException {
		String urlPrefix = "http://";
		URI uri;
		URI fullUri = null;

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
		return fullUri;
	}

	/**
	 * Sets the the event time parameter in the URI for the GetObservation
	 * HTTP-GET request
	 * 
	 * @param requestUri
	 *            URI for which the event time parameter has to be set
	 * @param eventTimeBegin
	 *            parameter for the event time beginning
	 * @return GetObservation HTTP-GET URI with set event time
	 * @throws URISyntaxException
	 */
	public URI setURIEventTimeParameter(URI requestUri, DateTime eventTimeBegin) throws URISyntaxException {
		URIBuilder builder = new URIBuilder(requestUri);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String formattedEventTimeBegin = formatter.print(eventTimeBegin);
		String formmettedEventTimeEnd = formatter.print(DateTime.now());
		String eventTimeValue = new StringBuilder(formattedEventTimeBegin).append("/").append(formmettedEventTimeEnd)
				.toString();
		requestUri = builder.setParameter(EVENT_TIME_KEY, eventTimeValue).build();
		return requestUri;
	}
}
