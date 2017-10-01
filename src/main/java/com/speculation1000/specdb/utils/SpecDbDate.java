package com.speculation1000.specdb.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SpecDbDate {
	
	/**
	 * @param instant
	 * @return instant rolled back to midnight epoch seconds 
	 */
	public static long getTodayMidnightEpochSeconds(Instant instant){
		Instant todayMidnight = getTodayMidnightInstant(instant);
		return todayMidnight.getEpochSecond();
	}
	
	/**
	 * @param instant
	 * @return the instant rolled back to midnight (i.e 01/01/2017:10:15 -> 01/01/2017:0:00)
	 */
	public static Instant getTodayMidnightInstant(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		return Instant.ofEpochSecond(today.toEpochSecond());
	}
	
	/**
	 * @param Instant
	 * @returns ISO_INSTANT Formatted String of Instant
	 * Default method for log string Time stamps 
	 */
	public static String instantToLogStringFormat(Instant instant){
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	
	/**
	 * @param long
	 * @returns ISO_INSTANT Formatted String of long
	 * Default method for log string Time stamps 
	 */
	public static String longToLogStringFormat(long epochSeconds){
		Instant instant = Instant.ofEpochSecond(epochSeconds);
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	
	/**
	 * @param instant
	 * @returns yesterdays date in epoch seconds
	 * Method converts the parameter to midnight (i.e. 01/01/17:0:01 -> 01/01/17:0:00)
	 * Returns the midnight instant - 86400 (1 day seconds)
	 */
	public static long getYesterdayEpochSeconds(Instant instant){
		long midnight = getTodayMidnightEpochSeconds(instant);
		return midnight - 86400;
	}
	
	/**
	 * @param long
	 * @returns String formatted date M/dd/yy K:mm a (9/13/2017 9:12 AM)
	 */
	public static String longToShortDateTimeStr(long epochSeconds){
		return instantToShortDateTimeStr(Instant.ofEpochSecond(epochSeconds));
	}
	
	/**
	 * @param Instant
	 * @returns String formatted date M/dd/yy K:mm a (9/13/2017 9:12 AM)
	 */
	public static String instantToShortDateTimeStr(Instant instant){
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ofPattern("M/dd/yy K:mm a (z)"));
	}
	
	/**
	 * @param long
	 * @returns String formatted date M/dd/yy (9/13/2017)
	 */
	public static String longToShortDateStr(long epochSeconds){
		return instantToShortDateStr(Instant.ofEpochSecond(epochSeconds));
	}
	
	/**
	 * @param Instant
	 * @returns String formatted date M/dd/yy (9/13/2017)
	 */
	public static String instantToShortDateStr(Instant instant){
		return ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC")).format(DateTimeFormatter.ofPattern("M/dd/yy"));
	}
	
	/**
	 * @param instant
	 * @return instant rolled back to last 6 hour block (00:00, 06:00, 12:00, 18:00)  
	 */
	public static long getLastSixHourSeconds(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));		
		int currentHour = zdt.getHour();
		
		int displayHour;		
		switch(currentHour){
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				displayHour = 8;
				break;
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				displayHour = 16;
				break;
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				displayHour = 23;
				break;
			default:
				displayHour = 23;
				break;
		}
		
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), displayHour, 0, 0, 0, ZoneId.of("Etc/UTC"));
		return today.toEpochSecond();
	}
	
	

}
