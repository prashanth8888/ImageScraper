package com.mediaproject.yahoo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import com.mediaproject.locationInfo.CityGeoLocation;
import com.mediaproject.locationInfo.woeIdGeoLocationMapper;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import twitter4j.JSONObject;

/**
 * @author David Hardtke
 * @author xyz Simple HTTP Request implementation
 */
public class YahooLocationFinder {

	private static Logger log = Logger.getLogger(YahooLocationFinder.class.getName());

	private String responseBody = "";

	private OAuthConsumer consumer = null;

	/** Default Constructor */
	public YahooLocationFinder() {
	}

	public YahooLocationFinder(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public HttpsURLConnection getConnection(String url) throws IOException, OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException {
		try {
			URL u = new URL(url);

			HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();

			if (consumer != null) {
				try {
					log.info("Signing the oAuth consumer");
					consumer.sign(uc);

				} catch (OAuthMessageSignerException e) {
					log.severe("Error signing the consumer" + e);
					throw e;

				} catch (OAuthExpectationFailedException e) {
					log.severe("Error signing the consumer" + e);
					throw e;

				} catch (OAuthCommunicationException e) {
					log.severe("Error signing the consumer" + e);
					throw e;
				}
				uc.connect();
			}
			return uc;
		} catch (IOException e) {
			log.severe("Error signing the consumer" + e);
			throw e;
		}
	}

	/**
	 * Sends an HTTP GET request to a url
	 *
	 * @param url the url
	 * @return - HTTP response code
	 */
	public int sendGetRequest(int woeId, String url) throws IOException, OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException {

		int responseCode = 500;
		try {
			HttpsURLConnection uc = getConnection(url);

			responseCode = uc.getResponseCode();

			if (200 == responseCode || 401 == responseCode || 404 == responseCode) {
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(responseCode == 200 ? uc.getInputStream() : uc.getErrorStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}

				rd.close();
				setResponseBody(sb.toString());
				mapLatitudeLongitude(sb.toString(), woeId);
			}
		} catch (MalformedURLException ex) {
			throw new IOException(url + " is not valid");
		} catch (IOException ie) {
			throw new IOException("IO Exception " + ie.getMessage());
		}
		return responseCode;
	}

	public void mapLatitudeLongitude(String response, int woeId) {
		
		JSONObject jsonResponseObj = new JSONObject(response);
		JSONObject jsonResultsWrapper = jsonResponseObj.getJSONObject("query");
		JSONObject jsonResultsArr = jsonResultsWrapper.getJSONObject("results");

		if (jsonResultsArr != null && jsonResultsArr.length() > 0) {
			
			JSONObject currentPlace = jsonResultsArr.getJSONObject("place");
			String placeName = currentPlace.getString("name");
			String latitude = currentPlace.getJSONObject("centroid").getString("latitude");
			String longtitude = currentPlace.getJSONObject("centroid").getString("longitude");
			woeIdGeoLocationMapper.woeIdGeoLocationMap.put(woeId, new CityGeoLocation(placeName, latitude, longtitude));
		
		}
	}

	/**
	 * Return the Response body
	 * 
	 * @return String
	 */
	public String getResponseBody() {
		return responseBody;
	}

	/**
	 * Setter
	 * 
	 * @param responseBody
	 */
	public void setResponseBody(String responseBody) {
		if (null != responseBody) {
			this.responseBody = responseBody;
		}
	}

	/**
	 * Set the oAuth consumer
	 * 
	 * @param consumer
	 */
	public void setOAuthConsumer(OAuthConsumer consumer) {
		this.consumer = consumer;
	}
}
