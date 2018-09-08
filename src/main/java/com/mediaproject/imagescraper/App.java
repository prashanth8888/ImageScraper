package com.mediaproject.imagescraper;

import java.io.IOException;
import java.util.logging.Logger;

import com.mediaproject.database.InitializeTwitterDB;
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
	private static InitializeTwitterDB twitterDBConnection;
	private static YahooAPIAuthenticator yahoolocationInfoApi;
//	private static Connection twitterDBConn;

	// Initialize Streaming factories
	public static void init() throws IOException {
//		twitterDBConn = twitterDBConnection.connect();
		streamingFactory = new StreamingFactory();
		twitterDBConnection = new InitializeTwitterDB();
		yahoolocationInfoApi = new YahooAPIAuthenticator();
	}

	public static void main(String[] args) throws Exception {
		logger.info("Starting the Main App");
		init();

		// Initialize the APIs
		TrendProcessorAsyncRunner trendProcessorAsyncRunner = (TrendProcessorAsyncRunner) streamingFactory
				.getHandler(Streamer.TwitterTrend);
		
		//Get the location Info
		yahoolocationInfoApi.setUpLocationGeoCodeInfo(LocationMapper.getLocationInfo());
		
		// Trending Info topics
//		trendProcessorAsyncRunner.getTrends(LocationMapper.getLocationInfo());
		
		logger.info("Main App has completed running");
	}
}
