package com.mediaproject.twitterClient;

import com.mediaproject.imagescraper.factory.StreamingFactory;

import twitter4j.Query;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class SearchProcessor extends StreamingFactory implements TwitterProcessor {

	public void TwitterConfig() throws TwitterException {
		Twitter twitter = TwitterFactory.getSingleton();
		Query query = new Query("Sample");
		twitter.search(query);
	}

}
