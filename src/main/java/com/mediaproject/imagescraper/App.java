package com.mediaproject.imagescraper;

import java.util.logging.Logger;

import com.mediaproject.imagescraper.factory.StreamingFactory;
import com.mediaproject.locationInfo.LocationMapper;
import com.mediaproject.streamingEnums.Streamer;
import com.mediaproject.twitterClient.TrendProcessorAsyncRunner;
import com.mediaproject.yahoo.api.YahooAPIAuthenticator;

/**
 * @author Prashanth Seralathan
 *
 */
public class App {
	private static Logger logger = Logger.getLogger(App.class.getName());
	private static StreamingFactory streamingFactory;

	// Initialize Streaming factories
	public static void init() {
		streamingFactory = new StreamingFactory();
	}

	public static void main(String[] args) throws Exception {
		logger.info("Starting the Main App");
		init();

		// Initialize the APIs
		TrendProcessorAsyncRunner trendProcessorAsyncRunner = (TrendProcessorAsyncRunner) streamingFactory
				.getHandler(Streamer.TwitterTrend);
		YahooAPIAuthenticator yahoolocationInfoApi = new YahooAPIAuthenticator();
		
		yahoolocationInfoApi.setUpLocationGeoCodeInfo(LocationMapper.getLocationInfo());
		// Trending Info topics
		trendProcessorAsyncRunner.getTrends(LocationMapper.getLocationInfo());
		logger.info("Main App has completed running");
	}
}
