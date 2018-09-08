package com.mediaproject.yahoo.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.mediaproject.database.InitializeTwitterDB;
import com.mediaproject.locationInfo.CityGeoLocation;
import com.mediaproject.locationInfo.WoeIdGeoLocationMapper;

/**
 *
 */
public class YahooAPIAuthenticator {

	public YahooAPIAuthenticator() {
		// Initialize DB Connection
		twitterDBConnection = InitializeTwitterDB.connect();
	}

	private Connection twitterDBConnection;

	private static Logger log = Logger.getLogger(YahooAPIAuthenticator.class.getName());

	protected static String yahooServer = "https://query.yahooapis.com/v1/public/yql";

	/** The HTTP request object used for the connection */
	private static YahooLocationFinder httpRequest = new YahooLocationFinder();
	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";

	private static final int HTTP_STATUS_OK = 200;

	public int getLocationDatafromYahoo(int woeId) throws UnsupportedEncodingException, Exception {

// Add query
		String params = yahooServer.concat("?q=");

// TODO: Research about URL encoding
		params = params.concat(
				URLEncoder.encode(this.getSearchString() + this.getWoeid(woeId) + this.getResultFormat(), "UTF-8")
						.replace("+", "%20").replace("%3D", "=").replace("%26", "&"));
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
		for (int woeId : woeIds) {
			getLocationDatafromYahoo(woeId);
		}

		Map<Integer, CityGeoLocation> locationMap = WoeIdGeoLocationMapper.woeIdGeoLocationMap;
		PreparedStatement statement = null;

		String insertLocationData = "INSERT INTO location(cityId,name,lattitude,longtitude,woeid) values (?, ?, ?, ?, ?)"
				+ "ON CONFLICT(cityId) DO UPDATE SET name = ? , lattitude = ?, longtitude = ?, woeId = ?";

		statement = twitterDBConnection.prepareStatement(insertLocationData);
		
		//TODO: Rewrite the SQL to have lesser number of parameters - Must be some SQL trick.
		int idx = 0;
		for (Entry<Integer, CityGeoLocation> locationEntry : locationMap.entrySet()) {
			Integer currentWoeId = locationEntry.getKey();
			CityGeoLocation currentGeoLocation = locationEntry.getValue();
			statement.setLong(1, idx);
			statement.setString(2, currentGeoLocation.getName());
			statement.setDouble(3, Double.parseDouble(currentGeoLocation.getLattitude()));
			statement.setDouble(4, Double.parseDouble(currentGeoLocation.getLongitude()));
			statement.setDouble(5, currentWoeId);
			
			//Update values
			statement.setString(6, currentGeoLocation.getName());
			statement.setDouble(7, Double.parseDouble(currentGeoLocation.getLattitude()));
			statement.setDouble(8, Double.parseDouble(currentGeoLocation.getLongitude()));
			statement.setDouble(9, currentWoeId);
			
			statement.executeUpdate();
			
			idx++;
		}
		
			twitterDBConnection.close();

	}
}