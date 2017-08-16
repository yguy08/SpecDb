package com.speculation1000.specdb.mode;

public interface Mode extends Runnable {
	
	void startRun();
	
	String getModeName();
	
}
