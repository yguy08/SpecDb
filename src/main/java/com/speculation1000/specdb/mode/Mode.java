package com.speculation1000.specdb.mode;

public interface Mode extends Runnable {
	
	String getStartRunMessage();
	
	String getEndRunMessage();
	
	void startApp();
	
}
