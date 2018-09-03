package com.mediaproject.twitterClient;

import java.util.List;

import com.mediaproject.imagescraper.factory.StreamingFactory;

import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterStreamer extends StreamingFactory {

	public void TwitterConfig() throws TwitterException {

		Twitter twitter = TwitterFactory.getSingleton();
		List<Status> statuses = twitter.getHomeTimeline();
		System.out.println("Showing home timeline.");

		for (Status status : statuses) {
			System.out.println(status.getUser().getName() + ":" + status.getText());
		}

	}

	public void getTrends(List<Integer> cities) throws TwitterException {
		Twitter twitter = TwitterFactory.getSingleton();

		for (int i = 0; i < cities.size(); i++) {
			int cityId = cities.get(i);

			Trends currentCityTrend = twitter.getPlaceTrends(cityId);
			Trend[] trends = currentCityTrend.getTrends();
			
			System.out.println("----------------------------");
			
			System.out.println("Current City" + currentCityTrend.getLocation().getName() + ","
					+ currentCityTrend.getLocation().getCountryName());

			for (Trend trend : trends) {
				System.out.println("Topic " + "->" + trend.getName());
				System.out.println("Query " + "->" + trend.getQuery());
				System.out.println("Volume " + "->" + trend.getTweetVolume());
			}
			
			System.out.println("----------------------------");

		}

	}

}
