package com.mediaproject.imagescraper.factory;

import java.util.HashMap;
import java.util.Map;

import com.mediaproject.streamingEnums.Streamer;
import com.mediaproject.twitterClient.TwitterStreamer;

public class StreamingFactory {

	public static Map<String, StreamingFactory> streamingFactoryMap = new HashMap<>();

	static {
		streamingFactoryMap.put("Twitter", new TwitterStreamer());
	}

	public StreamingFactory getHandler(Streamer twitter) {
		return streamingFactoryMap.get(twitter.name());
	}
}
