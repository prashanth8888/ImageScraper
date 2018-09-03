package com.mediaproject.twitterClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.mediaproject.twitter.mappers.TrendsCollection;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TrendProcessor implements TwitterProcessor, Callable<TrendsCollection> {

	private static Integer TRENDS_LIMIT = 10;
	private static Logger logger = Logger.getLogger(TrendProcessor.class.getName());
	private int cityId;

	public TrendProcessor(int cityId) {
		this.cityId = cityId;
	}

	Comparator<Trend> trendComparator = new Comparator<Trend>() {

		@Override
		public int compare(Trend trend1, Trend trend2) {
			return trend2.getTweetVolume() - trend1.getTweetVolume();
		}
	};

	public TrendsCollection getTrends(int cityId) throws TwitterException {

		logger.info("Executing city " + cityId);

		Twitter twitter = TwitterFactory.getSingleton();
		Trends currentCityTrend = twitter.getPlaceTrends(cityId);
		Trend[] trends = currentCityTrend.getTrends();
		logger.info("Retrieved Trends for " + currentCityTrend.getLocation().getName());
		Arrays.sort(trends, trendComparator);
		return processTrendInfo(trends, currentCityTrend.getLocation().getName());
	}

	public TrendsCollection processTrendInfo(Trend[] trends, String locationName) {

		TrendsCollection trendsCollection = new TrendsCollection();
		trendsCollection.setCityId(cityId);
		trendsCollection.setDateOfTrend(new Date());
		trendsCollection.setLocationName(new String(locationName));

		trendsCollection.setName(new ArrayList<>(
				Arrays.stream(trends).limit(TRENDS_LIMIT).map(trend -> trend.getName()).collect(Collectors.toList())));

		trendsCollection.setQuery(new ArrayList<>(
				Arrays.stream(trends).limit(TRENDS_LIMIT).map(trend -> trend.getQuery()).collect(Collectors.toList())));

		trendsCollection.setTweetVolume(new ArrayList<>(Arrays.stream(trends).limit(TRENDS_LIMIT)
				.map(trend -> trend.getTweetVolume()).collect(Collectors.toList())));

		return trendsCollection;

	}

	@Override
	public TrendsCollection call() throws Exception {
		return getTrends(cityId);
	}

}
