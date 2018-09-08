package com.mediaproject.twitterClient;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.mediaproject.imagescraper.factory.StreamingFactory;
import com.mediaproject.locationInfo.CityGeoLocation;
import com.mediaproject.locationInfo.WoeIdGeoLocationMapper;
import com.mediaproject.twitter.mappers.TrendsCollection;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Query.Unit;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class SearchProcessor extends StreamingFactory implements TwitterProcessor {
	private static Logger logger = Logger.getLogger(SearchProcessor.class.getName());
	private static final double RADIUS = 15.0;
	private static final Unit miles = Unit.mi;

	public synchronized void TwitterConfig(TrendsCollection trendsCollection) throws TwitterException {

		Twitter twitter = TwitterFactory.getSingleton();

		logger.info("--------------------------------------");
		logger.info("Retrieving Tweets for the City " + trendsCollection.getLocationName());

		CityGeoLocation coordinatesInfo = WoeIdGeoLocationMapper.woeIdGeoLocationMap.get(trendsCollection.getCityId());
		Query query = new Query();
		GeoLocation location = null;

		if (coordinatesInfo != null) {
			location = new GeoLocation(Double.parseDouble(coordinatesInfo.getLattitude()),
					Double.parseDouble(coordinatesInfo.getLongitude()));
		}

		Iterator<String> trendQueries = trendsCollection.getQuery().iterator();

		while (trendQueries.hasNext()) {
			String queryString = trendQueries.next();
			logger.info("Query " + queryString);
			query.setQuery(queryString);
			if (location != null) {
				query.setGeoCode(location, RADIUS, miles);
			}
			processResults(twitter.search(query));

		}

		logger.info("Wrapping up results for the for the City " + trendsCollection.getLocationName());
		logger.info("--------------------------------------");

	}

	public void processResults(QueryResult searchResult) {

		List<Status> tweets = searchResult.getTweets();
		for (Status tweet : tweets) {
			System.out.println("User " + tweet.getUser().getName());
			System.out.println("Text " + tweet.getText());
		}

	}

}
