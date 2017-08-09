package com.speculation1000.specdb.start;

import java.time.Instant;

public class StartRun {
	
	private static Instant startRunTS = null;
	
	public static void setStartRunTS(){
		startRunTS = Instant.now();
	}
	
	public static Instant getStartRunTS(){
		//For testing purposes, don't need to set start every time
		if(startRunTS != null){
			return startRunTS;
		}else{
			setStartRunTS();
			return startRunTS;
		}
	}

}
