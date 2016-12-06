package org.n52.sensorweb.sos.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DataReceiver {

	/**
	 * Receive the raw byte data from the GetObservation request
	 * 
	 * @param httpGet
	 *            HTTP-GET request for the GetObservation request
	 */
	public byte[] receiveData(HttpGet httpGet) throws IOException {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		byte[] data = null;
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream inStream = entity.getContent();
			data = getByteArrayFromInputStream(inStream);
		} finally {
			httpclient.close();
			response.close();
		}
		return data;
	}

	/**
	 * Creates an array of bytes from an InputStream
	 * 
	 * @param is
	 *            InputStream
	 * @return byte array
	 */
	private byte[] getByteArrayFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nBytesRead;
		int size = 1024;
		byte[] data = new byte[size];

		/*
		 * Write data.length bytes repeatedly from the InputStream into a
		 * ByteBuffer until the stream is at the end of file
		 */
		while ((nBytesRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nBytesRead);
		}
		buffer.flush();

		return buffer.toByteArray();
	}
}
