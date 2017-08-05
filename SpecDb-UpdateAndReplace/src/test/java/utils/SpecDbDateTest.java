package utils;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class SpecDbDateTest {
	
	@Test
	public void testIsNewDay(){
		Instant instant = Instant.now();
		ZoneId z = ZoneId.of( "Etc/UTC" );
		ZonedDateTime zdt = instant.atZone(z);
		int hour = zdt.getHour();
		if(SpecDbDate.isNewDay()){
			assertTrue(hour == 0);
		}else{
			assertTrue(hour != 0);
		}
	}

}
