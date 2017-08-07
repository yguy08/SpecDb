package com.speculation1000.specdb.utils;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class SpecDbDatesTest {
	
	@Test
	public void testIsNewDate(){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		Instant start = Instant.ofEpochSecond(today.toEpochSecond());
		
		assertTrue(SpecDbDate.isNewDay(start));
		
		Instant next = null;
		for(int i = 60; i < 60 * 24;i+=60){
			next = start.plusSeconds(i*60);
			assertFalse(SpecDbDate.isNewDay(next));	
		}
		
		Instant last = next.plusSeconds(60 * 60);
		assertTrue(SpecDbDate.isNewDay(last));
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
}
