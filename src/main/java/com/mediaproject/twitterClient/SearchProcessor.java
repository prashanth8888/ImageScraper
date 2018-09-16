package com.mediaproject.twitterClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.mediaproject.database.InitializeTwitterDB;
import com.mediaproject.imagescraper.factory.StreamingFactory;
import com.mediaproject.locationInfo.CityGeoLocation;
import com.mediaproject.locationInfo.WoeIdGeoLocationMapper;
import com.mediaproject.twitter.mappers.TrendsCollection;
import com.vdurmont.emoji.EmojiParser;

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
	private static String SQL_INSERT_TWEET = "INSERT into tweetuser(topicidref, userid, tweet_text, profile_pic, lattitude, longtitude, place_name, street_address, country) \n"
			+ "VALUES(?,?,?,?,?,?,?,?,?)";
	private static InitializeTwitterDB initializeTwitterDB;
	private static Connection twitterDBConncetion;
	

	public SearchProcessor() {
		try {
			initializeTwitterDB = new InitializeTwitterDB();
		} catch (Exception e) {
			logger.severe("Error getting DB instance " + e.getMessage());
		}

		twitterDBConncetion = InitializeTwitterDB.connect();
	}

	public synchronized void TwitterConfig(TrendsCollection trendsCollection) throws TwitterException, SQLException {

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

		for (int i = 0; i < trendsCollection.getQuery().size(); i++) {
			String queryString = trendsCollection.getQuery().get(i);
			long topicId = trendsCollection.getTopicId().get(i);
			logger.info("Query " + queryString);

			query.setQuery(queryString);
			if (location != null) {
				query.setGeoCode(location, RADIUS, miles);
			}

			processResults(twitter.search(query), topicId);

		}

		logger.info("Wrapping up results for the for the City " + trendsCollection.getLocationName());
		logger.info("--------------------------------------");

	}

	public void processResults(QueryResult searchResult, long topicIdRef) throws SQLException{

		PreparedStatement statement = null;
		try {
			statement = twitterDBConncetion.prepareStatement(SQL_INSERT_TWEET);
			List<Status> tweets = searchResult.getTweets();

			for (Status tweet : tweets) {
				
				statement.setLong(1, topicIdRef);
				statement.setLong(2, tweet.getUser().getId());
				statement.setString(3, EmojiParser.removeAllEmojis(tweet.getText()));
				statement.setString(4, tweet.getUser().get400x400ProfileImageURLHttps());
				
				boolean validLoc = Optional.ofNullable(tweet.getGeoLocation()).isPresent();
				
				if(validLoc) {
					statement.setDouble(5, tweet.getGeoLocation().getLatitude());
					statement.setDouble(6, tweet.getGeoLocation().getLongitude());
				} else {
					statement.setNull(5, Types.DOUBLE);
					statement.setNull(6, Types.DOUBLE);
				}
				
				
				boolean validPlace = Optional.ofNullable(tweet.getPlace()).isPresent();
				
				if(validPlace) {
					statement.setString(7,	tweet.getPlace().getName());
					statement.setString(8, 	tweet.getPlace().getStreetAddress());
					statement.setString(9, 	tweet.getPlace().getCountry());
				} else {
					statement.setNull(7,  Types.VARCHAR);
					statement.setNull(8,  Types.VARCHAR);
					statement.setNull(9,  Types.VARCHAR);
				}
				
				if((statement.execute()))
					logger.info("Fine ->" + statement.toString());
					
			}
			
		} catch (SQLException e1) {
			logger.info("Probably the UserID/TopicID combination exists " + e1.getMessage());
		} catch(Exception e) {
			logger.severe("Exception while inserting tweet info " + e.getMessage());
		} finally {
			statement.close();
		}

	}

}
