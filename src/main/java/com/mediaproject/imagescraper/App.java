package com.mediaproject.imagescraper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
	private StreamingFactory streamingFactory;
	private InitializeTwitterDB twitterDBConnection;
	private YahooAPIAuthenticator yahoolocationInfoApi;
	private TrendProcessorAsyncRunner trendProcessorAsyncRunner;
	private static Integer THREAD_POOL_SIZE = 5;

	public void init() throws IOException {
		streamingFactory = new StreamingFactory();
		twitterDBConnection = new InitializeTwitterDB();
		yahoolocationInfoApi = new YahooAPIAuthenticator();
		trendProcessorAsyncRunner = (TrendProcessorAsyncRunner) streamingFactory.getHandler(Streamer.TwitterTrend);
//		yahoolocationInfoApi.setUpLocationGeoCodeInfo(LocationMapper.getLocationInfo());
	}

	public static void main(String[] args) throws Exception {
		logger.info("Starting the Main App");

		App trendStreamerApp = new App();
		trendStreamerApp.init();

		Runnable streamingWorkerThread = new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("Starting Trends Streaming Execution @ " + new Date().toString());
					trendStreamerApp.trendProcessorAsyncRunner.getTrends(LocationMapper.getLocationInfo());
				} catch (SQLException e) {
					e.printStackTrace();
					logger.severe("SQL Error while processing the Trends");
				} catch (Exception e) {
					e.printStackTrace();
					logger.severe("Error while processing the trends");
				}
			}
		};

		ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
		scheduledExecutor.scheduleWithFixedDelay(streamingWorkerThread, 0, 15, TimeUnit.MINUTES);

		logger.info("Main App has completed running");
	}
}
