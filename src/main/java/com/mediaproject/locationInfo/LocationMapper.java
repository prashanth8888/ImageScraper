package com.mediaproject.locationInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.mediaproject.database.InitializeTwitterDB;

public class LocationMapper {
	
	
	private static Map<Integer, Integer> locationWoeIdMap = new HashMap<>();
	
	
	public static Map<Integer, Integer> getLocationInfo() throws SQLException {
		
		Connection twitterDBConnection = InitializeTwitterDB.connect();
		
		String getLocationData = "Select woeid, cityId from location";
		Statement statement = twitterDBConnection.createStatement();
		
		ResultSet results = statement.executeQuery(getLocationData);
		while(results.next()) {
			locationWoeIdMap.put(results.getInt(1), results.getInt(2));
		}		
		
		twitterDBConnection.close();
		
		return locationWoeIdMap;
	}
	
}
