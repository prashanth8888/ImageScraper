package com.mediaproject.imagescraper.factory;

import java.util.HashMap;
import java.util.Map;

import com.mediaproject.streamingEnums.Streamer;
import com.mediaproject.twitterClient.SearchProcessor;
import com.mediaproject.twitterClient.TrendProcessor;
import com.mediaproject.twitterClient.TwitterProcessor;

public class StreamingFactory {

	public static Map<String, TwitterProcessor> streamingFactoryMap = new HashMap<>();

	static {
		streamingFactoryMap.put("TwitterSearch", new SearchProcessor());
		streamingFactoryMap.put("TwitterTrend", new TrendProcessor());
	}

	public TwitterProcessor getHandler(Streamer twitter) {
		return streamingFactoryMap.get(twitter.name());
	}
}
