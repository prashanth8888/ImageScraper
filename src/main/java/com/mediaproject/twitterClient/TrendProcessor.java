package com.mediaproject.twitterClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TrendProcessor implements TwitterProcessor {
	
	private static Integer TRENDS_LIMIT = 10;
	private static Logger logger = Logger.getLogger(TrendProcessor.class.getName());
	
	Comparator<Trend> trendComparator = new Comparator<Trend>() {
		
		@Override
		public int compare(Trend trend1, Trend trend2) {
			return trend2.getTweetVolume() - trend1.getTweetVolume();
		}
	};
	 
	
	public void getTrends(List<Integer> cities) throws TwitterException {
		Twitter twitter = TwitterFactory.getSingleton();

		for (int i = 0; i < cities.size(); i++) {
			
			int cityId = cities.get(i);

			Trends currentCityTrend = twitter.getPlaceTrends(cityId);
			Trend[] trends = currentCityTrend.getTrends();
			Arrays.sort(trends, trendComparator);
			System.out.println("----------------------------");
			System.out.println("Current City " + currentCityTrend.getLocation().getName());
			processTrendInfo(trends);
			System.out.println("----------------------------");

		}

	}
	
	
	public void processTrendInfo(Trend[] trends) {

		for (int j = 0; j < TRENDS_LIMIT && j < trends.length ; j++) {
			logger.info("Topic " + "->" + trends[j].getName());
			logger.info("Query " + "->" + trends[j].getQuery());
			logger.info("Volume " + "->" + trends[j].getTweetVolume());
		}
		
	}
	
}
