package com.speculation1000.specdb.utils;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.speculation1000.specdb.start.SpecDbDate;

public class SpecDbDatesTest {
	
	@Test
	public void testIsNewDate(){
	    ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
	    ZonedDateTime startYear = ZonedDateTime.of(zdt.getYear(), 1, 1, 0, 14, 0, 0, ZoneId.of("Etc/UTC"));
	    Instant start = Instant.ofEpochSecond(startYear.toEpochSecond());

	    for(int i = 0; i < 15 * 4 * 24 * 365;i+=15){
		ZonedDateTime next = ZonedDateTime.ofInstant(start.plusSeconds(i*60), ZoneId.of("Etc/UTC"));
		int hour = next.getHour();
		int min = next.getMinute();
		if(hour == 0 && min < 15){
			assertTrue(SpecDbDate.isNewDay(Instant.ofEpochSecond(next.toEpochSecond())));
			System.out.println(next.toLocalDate());
		}else{
			assertFalse(SpecDbDate.isNewDay(Instant.ofEpochSecond(next.toEpochSecond())));
		}
        }
    }
	
	@Test
	public void testGetYesterdayEpochSeconds(){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime midnight = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		
		Instant start = Instant.ofEpochSecond(midnight.toEpochSecond());
		long yesterday = SpecDbDate.getYesterdayEpochSeconds(start);
		long today = start.getEpochSecond();
		assertTrue(today - yesterday == 86400);
		
		for(int i = 1; i < 30;i++){
			today = start.plusSeconds(60 * 60 * 24 * i).getEpochSecond();
			yesterday = SpecDbDate.getYesterdayEpochSeconds(Instant.ofEpochSecond(today));
			assertTrue(today - yesterday == 86400);
		}
		
	}
	
	@Test
	public void testNextHourInitialDelay(){
		ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime startHour = start.withHour(start.getHour()).withMinute(0).withSecond(0).withNano(0);
		ZonedDateTime nextHour = start.withHour(start.getHour()).withMinute(0).withSecond(0).withNano(0).plusHours(1);
		Duration duration = Duration.between(startHour, nextHour);
		int mins = (int) duration.toMinutes();
		assertEquals(60, mins);
		for(int i = 1; i < 60; i++){
			ZonedDateTime nextMin = startHour.plusMinutes(i);
			int delay = SpecDbDate.nextHourInitialDelay(Instant.ofEpochSecond(nextMin.toEpochSecond()));
			assertEquals(60 - i, delay);
		}
	}
}
