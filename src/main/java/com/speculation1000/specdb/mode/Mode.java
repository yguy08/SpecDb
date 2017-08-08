package com.speculation1000.specdb.mode;

import java.time.Instant;

public interface Mode {
	
	void setStartRunTS(Instant instant);
	
	Instant getStartRunTS();
	
	String getStartRunMessage();
	
	String getEndRunMessage();
	
	void startApp();
	
}
