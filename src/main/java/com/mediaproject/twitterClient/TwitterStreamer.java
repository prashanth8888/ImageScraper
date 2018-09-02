package com.mediaproject.twitterClient;

import java.util.List;

import com.mediaproject.imagescraper.factory.StreamingFactory;

import twitter4j.Status;
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

}
