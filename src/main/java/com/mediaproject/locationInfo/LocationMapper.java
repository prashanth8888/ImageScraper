package com.mediaproject.locationInfo;

import java.util.ArrayList;
import java.util.List;

public class LocationMapper {
	private static List<Integer> woeIDs = new ArrayList<>();
	
	public static List<Integer> getLocationInfo() {
		
		woeIDs.add(LocationWOEIds.New_York);
		woeIDs.add(LocationWOEIds.San_Francisco);
		woeIDs.add(LocationWOEIds.Seattle);
		
		return woeIDs;
	}
}
