package com.speculation1000.specdb.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SpecDbDate {
	
	public static long getTodayUtcEpochSeconds(){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		return today.toEpochSecond();
	}
	
	/*
	 * @param Instant
	 * @returns ISO_INSTANT Formatted String of Instant
	 * Default method for log string Time stamps 
	 */
	public static String instantToLogStringFormat(Instant instant){
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ISO_INSTANT);
	}

	public static boolean isNewDay(Instant instant) {
		ZoneId z = ZoneId.of( "Etc/UTC" );
		ZonedDateTime zdt = instant.atZone(z);
		int hour = zdt.getHour();
		if(hour == 0){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * @param instant
	 * @returns yesterdays date in epoch seconds
	 * Used to help clean up
	 */
	public static long getYesterdayEpochSeconds(Instant instant){
		long yesterday = instant.getEpochSecond() - 86400;
		return yesterday;
	}

}
