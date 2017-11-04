package com.speculation1000.specdb.marketdata;

public interface ITickQueue {
	void put(ITick tick) throws InterruptedException;
	
	ITick take() throws InterruptedException;
}
