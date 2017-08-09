package com.speculation1000.specdb.utils;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.speculation1000.specdb.start.SpecDbTime;

public class SpecDbTimeTest {
	
	@Test
	public void testGetQuickModeDelaySeconds(){
		ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime startHour = start.withHour(start.getHour()).withMinute(0).withSecond(0).withNano(0);
		for(int i = 0; i < 60 * 60; i++){
			ZonedDateTime nextSecond = startHour.plusSeconds(i);
			long delay = SpecDbTime.getQuickModeDelaySeconds(Instant.ofEpochSecond(nextSecond.toEpochSecond()));
			long diff;			
			if(i < 14 * 60){
				diff = (14 * 60) - (i);
				assertEquals(diff, delay);
			}else if(i < 29 * 60){
				diff = (29 * 60) - (i);
				assertEquals(diff, delay);
			}else if(i < 44 * 60){
				diff = (44 * 60) - (i);
				assertEquals(diff, delay);
			}else if(i < 59 * 60){
				diff = (59 * 60) - (i);
				assertEquals(diff, delay);
			}else if(i < 74 * 60){
				diff = (74 * 60) - (i);
				assertEquals(diff, delay);
			}
		}
	}
	
}
