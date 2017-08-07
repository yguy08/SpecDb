package com.speculation1000.specdb.mode;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.speculation1000.specdb.log.SpecDbLogger;

public class NormalMode implements Runnable, Mode {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
		
	private static Instant startRunTS = null;

	@Override
	public void setStartRunTS(Instant instant) {
		startRunTS = instant;		
	}

	@Override
	public Instant getStartRunTS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStartRunMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEndRunMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startApp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
