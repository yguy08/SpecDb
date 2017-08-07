package loader;

import java.time.Instant;

public interface Mode {
	
	void setStartRunTS(Instant instant);
	
	Instant getStartRunTS();
	
	String getStartRunMessage();
	
	String getEndRunMessage();
	
	void startApp();
	
}
