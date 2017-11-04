package com.speculation1000.specdb.marketdata;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PoloniexTickQueue implements ITickQueue {
	
	private BlockingQueue<PoloniexTick> queue;
	
	public PoloniexTickQueue() {
		queue = new LinkedBlockingQueue<>(50);
	}

	@Override
	public void put(ITick tick) throws InterruptedException {
		queue.put((PoloniexTick) tick);
	}

	@Override
	public ITick take() throws InterruptedException {
		return queue.take();
	}

}
