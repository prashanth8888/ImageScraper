package com.mediaproject.yahoo.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;


/**
 *
 */
public class YahooAPIAuthenticator {

	private static Logger log = Logger.getLogger(YahooAPIAuthenticator.class.getName());


//	protected static String yahooServer = "https://yboss.yahooapis.com/geo/";
	protected static String yahooServer = "https://query.yahooapis.com/v1/public/yql";
	
	/** The HTTP request object used for the connection */
	private static YahooLocationFinder httpRequest = new YahooLocationFinder();
	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";

	private static final int HTTP_STATUS_OK = 200;

	/**
	 *
	 * @return
	 */
	public int getLocationDatafromYahoo(int woeId) throws UnsupportedEncodingException, Exception {

// Add query
		String params = yahooServer.concat("?q=");
		
// TODO: Research about URL encoding
		params = params.concat(URLEncoder.encode(this.getSearchString()+this.getWoeid(woeId)+this.getResultFormat(), "UTF-8").replace("+", "%20")
				.replace("%3D", "=")
				.replace("%26", "&")
				);
		String url = params;

		try {
			log.info("sending get request to " + URLDecoder.decode(url, ENCODE_FORMAT));
			int responseCode = httpRequest.sendGetRequest(woeId, url);

// Send the request
			if (responseCode == HTTP_STATUS_OK) {
				log.info("Response ");
			} else {
				log.severe("Error in response due to status code = " + responseCode);
			}
			log.info(httpRequest.getResponseBody());

		} catch (UnsupportedEncodingException e) {
			log.severe("Encoding/Decording error");
		} catch (IOException e) {
			log.severe("Error with HTTP IO" + e);
		} catch (Exception e) {
			log.severe(httpRequest.getResponseBody() + e);
			return 0;
		}

		return 1;

	}

	private String getSearchString() {
		return "SELECT name, centroid.latitude, centroid.longitude FROM geo.places where";
	}
	
	
	private String getWoeid(int woeId) {
		return " woeid=" + woeId;
	}
	
	private String getResultFormat() {
		return "&format=json";
	}
	
	public void setUpLocationGeoCodeInfo(List<Integer> woeIds) throws UnsupportedEncodingException, Exception {
		for(int woeId : woeIds) {
			getLocationDatafromYahoo(woeId);
		}
	}
}