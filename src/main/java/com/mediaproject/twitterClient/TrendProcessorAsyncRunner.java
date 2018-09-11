package com.mediaproject.twitterClient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.mediaproject.database.InitializeTwitterDB;
import com.mediaproject.twitter.mappers.TrendsCollection;

import twitter4j.TwitterException;

public class TrendProcessorAsyncRunner implements TwitterProcessor {
	private static Logger logger = Logger.getLogger(TrendProcessorAsyncRunner.class.getName());
	private static Connection twitterDBConncetion;
	private static InitializeTwitterDB initializeTwitterDB;
	private static SearchProcessor searchProcessor;
	private static String SQL_POPULATE_TRENDINGTOPICS = "INSERT INTO trendingtopics(cityrefid, trendingdate, topic, topicId, nooftweets) "
			+ "VALUES(?,?,?,DEFAULT,?) returning topicId"; 
	
	public TrendProcessorAsyncRunner() {
		try {
			initializeTwitterDB = new InitializeTwitterDB();
		} catch (Exception e) {
			logger.severe("Error getting DB instance " + e.getMessage());
		}

		twitterDBConncetion = InitializeTwitterDB.connect();
		searchProcessor = new SearchProcessor();
	}

	// Inject Search processor for use

	public static int TrendProcessorThreadResoruce = 10;

	public void getTrends(Map<Integer, Integer> citiesInfo) {

		ExecutorService executor = Executors.newFixedThreadPool(TrendProcessorThreadResoruce);
		List<Future<TrendsCollection>> trendsResultSet = new ArrayList<>();

		for (Map.Entry<Integer, Integer> cityInfo : citiesInfo.entrySet()) {
			int cityWoeId = cityInfo.getKey();
			Callable<TrendsCollection> trendsCollector = new TrendProcessor(cityInfo.getKey(), cityInfo.getValue());
			logger.info("Added " + cityWoeId + " for processing");
			Future<TrendsCollection> future = executor.submit(trendsCollector);
			trendsResultSet.add(future);
		}

		for (Future<TrendsCollection> future : trendsResultSet) {
			TrendsCollection currentTrendsCollection = null;
			try {
				currentTrendsCollection = future.get();
				persistTrendInfo(currentTrendsCollection);
				logger.info(currentTrendsCollection.toString());
			} catch (InterruptedException e) {
				logger.severe("Possible error in Thread Scheduling ");
				e.printStackTrace();
			} catch (Exception e) {
				logger.severe("Execption in TrendProcessorAsync Runner");
				e.printStackTrace();
			}
		}

	}

	public boolean persistTrendInfo(TrendsCollection currentCollection) throws SQLException, TwitterException {
		
		PreparedStatement statement = twitterDBConncetion.prepareStatement(SQL_POPULATE_TRENDINGTOPICS);
		int cityId = currentCollection.getCityId();
		currentCollection.setTopicId(new ArrayList<>());
		
		for (int i = 0; i < currentCollection.getNames().size(); i++) {
			
			statement.setInt(1, cityId);
			statement.setDate(2, new Date(Calendar.getInstance().getTimeInMillis()));
			statement.setString(3, currentCollection.getNames().get(i));
			if (currentCollection.getTweetVolume() != null && currentCollection.getTweetVolume().get(i) != null)
				statement.setLong(4, currentCollection.getTweetVolume().get(i));
			
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				//Gets the auto-generated Topic id
				currentCollection.getTopicId().add(result.getLong(1)); 
			}	
		}

		// Get the Tweet Info
		searchProcessor.TwitterConfig(currentCollection);
		return true;
	}

}
