package com.speculation1000.specdb.marketdata;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

	public static void main(String[] args) {
		
		ITickQueue tickQueue = new PoloniexTickQueue();
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		
		final ITickProducer producer = new PoloniexTickProducer(tickQueue);
	      executorService.submit(() -> {
	        while (true) {
	          producer.produce();
	        }
	      });
        
	    final ITickConsumer consumer = new PoloniexTickConsumer(tickQueue);
	    executorService.submit(() -> {
	    	while(true) {
	    		consumer.consume();
	    	}
	    });
		
	}

}
