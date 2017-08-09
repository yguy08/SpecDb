package com.speculation1000.specdb.utils;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.speculation1000.specdb.start.SpecDbTime;

public class SpecDbTimeTest {
	
	@Test
	public void testGetQuickModeDelay(){
		ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime startHour = start.withHour(start.getHour()).withMinute(0).withSecond(0).withNano(0);
		ZonedDateTime nextQuarter = startHour.plusMinutes(14);
		Duration duration = Duration.between(startHour, nextQuarter);
		int mins = (int) duration.toMinutes();
		assertEquals(14, mins);
		for(int i = 1; i < 60; i++){
			ZonedDateTime nextMin = startHour.plusMinutes(i);
			long delay = SpecDbTime.getQuickModeDelay(Instant.ofEpochSecond(nextMin.toEpochSecond()));
			if(i < 15){ 
				assertEquals(14 - i, delay);
			}else if(i < 30){
				assertEquals(29 - i, delay);
			}else if(i < 45){
				assertEquals(44 - i, delay);
			}else{
				assertEquals(59 - i, delay);
			}
		}
	}

}
