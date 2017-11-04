package com.speculation1000.specdb.marketdata;

public class PoloniexTickConsumer implements ITickConsumer {
	
	private final ITickQueue queue;
	
	public PoloniexTickConsumer(ITickQueue queue) {
		this.queue = queue;
	}

	@Override
	public void consume() throws InterruptedException {
        System.out.println(queue.take().toString());
	}

}
