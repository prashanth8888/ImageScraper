package com.mediaproject.locationInfo;

public class CityGeoLocation {
	
	private String lattitude;
	
	private String longitude;
	
	private String name;
	
	public CityGeoLocation(String name, String latitude, String longitude) {
		this.lattitude = latitude;
		this.longitude = longitude;
		this.name = name;
	}
	
	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
