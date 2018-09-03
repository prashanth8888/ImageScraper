package com.mediaproject.twitter.mappers;

import java.util.Date;
import java.util.List;

/**
 * 
 * Holds a collection of Trends objects. Combination based on
 * CityId/dateOfTrend/query
 *
 */
public class TrendsCollection {

	private int cityId;

	private String locationName;

	private Date dateOfTrend;

	private List<String> name;

	private List<String> query;

	private List<String> hashTag;

	private List<Integer> tweetVolume;

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Date getDateOfTrend() {
		return dateOfTrend;
	}

	public void setDateOfTrend(Date dateOfTrend) {
		this.dateOfTrend = dateOfTrend;
	}

	public List<String> getName() {
		return name;
	}

	public void setName(List<String> name) {
		this.name = name;
	}

	public List<String> getQuery() {
		return query;
	}

	public void setQuery(List<String> query) {
		this.query = query;
	}

	public List<String> getHashTag() {
		return hashTag;
	}

	public void setHashTag(List<String> hashTag) {
		this.hashTag = hashTag;
	}

	public List<Integer> getTweetVolume() {
		return tweetVolume;
	}

	public void setTweetVolume(List<Integer> tweetVolume) {
		this.tweetVolume = tweetVolume;
	}

	@Override
	public String toString() {
		StringBuilder trendResult = new StringBuilder();
		trendResult.append("Location : " + locationName).append("\n");
		trendResult.append("Query Names: " + name.toString()).append("\n");
		return trendResult.toString();
	}

}
