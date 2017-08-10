package com.speculation1000.specdb.time;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SpecDbDate {
	
	public static long getTodayUtcEpochSeconds(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		return today.toEpochSecond();
	}
	
	public static Instant getTodayMidnightInstant(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		return Instant.ofEpochSecond(today.toEpochSecond());
	}
	
	/*
	 * @param Instant
	 * @returns ISO_INSTANT Formatted String of Instant
	 * Default method for log string Time stamps 
	 */
	public static String instantToLogStringFormat(Instant instant){
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	
	/*
	 * @param long
	 * @returns ISO_INSTANT Formatted String of long
	 * Default method for log string Time stamps 
	 */
	public static String longToLogStringFormat(long epochSeconds){
		Instant instant = Instant.ofEpochSecond(epochSeconds);
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	public static boolean isNewDay(Instant instant) {
		ZoneId z = ZoneId.of( "Etc/UTC" );
		ZonedDateTime zdt = instant.atZone(z);
		int hour = zdt.getHour();
		int min = zdt.getMinute();
		if(hour == 0 && min < 15){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * @param instant
	 * @returns yesterdays date in epoch seconds
	 * Method converts the parameter to midnight (i.e. 01/01/17:0:01 -> 01/01/17:0:00)
	 * Returns the midnight instant - 86400 (1 day seconds)
	 */
	public static long getYesterdayEpochSeconds(Instant instant){
		long midnight = getTodayUtcEpochSeconds(instant);
		return midnight - 86400;
	}
	
	public static int nextHourInitialDelay(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));
		ZonedDateTime nextHour = zdt.withHour(zdt.getHour()).withMinute(0).withSecond(0).withNano(0).plusHours(1);
		Duration duration = Duration.between(zdt, nextHour);
        int initalDelay = (int) duration.toMinutes();
		return initalDelay;
	}
	
	public static void main(String[]args){
		System.out.println("Today: " + getTodayUtcEpochSeconds(Instant.now()));
		System.out.println("Yesterday: " + getYesterdayEpochSeconds(Instant.now()));
	}

}
