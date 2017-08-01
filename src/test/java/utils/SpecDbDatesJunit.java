package utils;

import static org.junit.Assert.*;

import java.util.Date;
import org.junit.Test;

public class SpecDbDatesJunit {
	
	@Test
	public void testDateToUtcMidnightSeconds(){
		//midnight utc dates
		Date date1 = new Date(1405728000);
		Date date2 = new Date(1405728000000L);
		Date date3 = new Date(1406851200);
		Date date4 = new Date(1406851200000L);
		
		//offsets dates
		Date date5 = new Date(1405742400);
		Date date6 = new Date(1405742400000L);
		Date date7 = new Date(1491811200);
		Date date8 = new Date(1491811200000L);
		
		//todays date - a date from today will equal next midnight
		long nowToUtcMidnight = SpecDbDates.dateToUtcMidnightSeconds(new Date(System.currentTimeMillis()));
		long now = new Date().getTime() / 1000;
		
		assertEquals(1405728000,SpecDbDates.dateToUtcMidnightSeconds(date1));
		assertEquals(1405728000,SpecDbDates.dateToUtcMidnightSeconds(date2));
		assertEquals(1406851200,SpecDbDates.dateToUtcMidnightSeconds(date3));
		assertEquals(1406851200,SpecDbDates.dateToUtcMidnightSeconds(date4));
		assertEquals(1405814400,SpecDbDates.dateToUtcMidnightSeconds(date5));
		assertEquals(1405814400,SpecDbDates.dateToUtcMidnightSeconds(date6));
		assertEquals(1491868800,SpecDbDates.dateToUtcMidnightSeconds(date7));
		assertEquals(1491868800,SpecDbDates.dateToUtcMidnightSeconds(date8));
		
		assertTrue(now < nowToUtcMidnight);
		
		
	}

}
