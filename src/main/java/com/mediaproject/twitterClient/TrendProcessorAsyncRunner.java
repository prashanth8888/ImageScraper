package com.mediaproject.twitterClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.mediaproject.twitter.mappers.TrendsCollection;

public class TrendProcessorAsyncRunner implements TwitterProcessor{
	private static Logger logger = Logger.getLogger(TrendProcessorAsyncRunner.class.getName());

	public static int TrendProcessorThreadResoruce = 3;

	public void getTrends(List<Integer> cities) {

		ExecutorService executor = Executors.newFixedThreadPool(TrendProcessorThreadResoruce);
		List<Future<TrendsCollection>> trendsResultSet = new ArrayList<>();

		for (int city : cities) {
			Callable<TrendsCollection> trendsCollector = new TrendProcessor(city);
			logger.info("Added " + city + " for processing");
			Future<TrendsCollection> future = executor.submit(trendsCollector);
			trendsResultSet.add(future);
		}

		// Obtain the results of Future - Print them out for fun
		for (Future<TrendsCollection> future : trendsResultSet) {
			TrendsCollection currentTrendsCollection = null;
			try {
				currentTrendsCollection = future.get();
				System.out.println(currentTrendsCollection.toString());
			} catch (InterruptedException e) {
				logger.severe("Possible error in Thread Scheduling ");
				e.printStackTrace();
			} catch (Exception e) {
				logger.severe("Execption in TrendProcessorAsync Runner");
				e.printStackTrace();
			}
		}

	}

}
