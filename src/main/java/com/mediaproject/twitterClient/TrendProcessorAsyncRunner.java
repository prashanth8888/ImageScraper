package com.mediaproject.twitterClient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
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

public class TrendProcessorAsyncRunner implements TwitterProcessor{
	private static Logger logger = Logger.getLogger(TrendProcessorAsyncRunner.class.getName());
	private static Connection twitterDBConncetion;
	
	public TrendProcessorAsyncRunner() {
		try {
			InitializeTwitterDB initializeTwitterDB = new InitializeTwitterDB();
		} catch(Exception e) {
			logger.severe("Error getting DB instance " + e.getMessage());
		}
		
		twitterDBConncetion = InitializeTwitterDB.connect();
	}
	
	//Inject Search processor for use
	private static SearchProcessor searchProcessor = new SearchProcessor();
	
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
//				searchProcessor.TwitterConfig(currentTrendsCollection);
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
	
	public boolean persistTrendInfo(TrendsCollection currentCollection) throws SQLException {
		String SQL_POPULATE_TRENDINGTOPICS = "INSERT INTO trendingtopics(cityrefid, trendingdate, topic, nooftweets) VALUES(?,?,?,?)";
		PreparedStatement statement = twitterDBConncetion.prepareStatement(SQL_POPULATE_TRENDINGTOPICS);
		int cityId = currentCollection.getCityId();
		
		for(int i = 0; i < currentCollection.getNames().size() ; i++) {
			statement.setInt(1, cityId);
			statement.setDate(2, new Date(Calendar.getInstance().getTimeInMillis()));
			statement.setString(3,  currentCollection.getNames().get(i));
			if(currentCollection.getTweetVolume() != null && currentCollection.getTweetVolume().get(i) != null)
				statement.setLong(4, currentCollection.getTweetVolume().get(i));
			statement.executeUpdate();
		}
		
		return true;
	}
	
//	public int getLastTopicId() throws SQLException {
//		String getLastTopicId = "Select topicid from trendingtopics order by topicid desc LIMIT 1";
//		Statement statement = twitterDBConncetion.prepareStatement(getLastTopicId);
//		ResultSet result = statement.getResultSet();
//		if(result.next()) {
//			return result.getInt(1);
//		}
//		//Default to start with
//		return 1;
//	}

}
