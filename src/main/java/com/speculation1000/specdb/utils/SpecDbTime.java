package com.speculation1000.specdb.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SpecDbTime {
	
	/**
	 *@param current instant
	 *@returns seconds to next quarter hour with SpecDb offset (14, 29, 44, 59) 
	 */
	public static long getQuickModeDelaySeconds(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));
		int minute = zdt.getMinute();
		ZonedDateTime nextQuarter;
		
		if(minute == 14 || minute == 29 || minute == 44 || minute == 59){
			return 900 - zdt.getSecond();
		}
		
		if(minute < 14){
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(14).withSecond(0).withNano(0);
		}else if(minute < 29){
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(29).withSecond(0).withNano(0);
		}else if(minute < 44){
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(44).withSecond(0).withNano(0);
		}else{
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(59).withSecond(0).withNano(0);
		}
		Duration duration = Duration.between(zdt, nextQuarter);
		return duration.getSeconds();		
	}
	
	/**
	 * Returns a pretty string for uptime
	 * @param long uptime seconds
	 * @returns String uptime seconds,minutes, hour or days
	 */
	public static String uptimePrettyStr(long uptimeSeconds){
		if(uptimeSeconds < 60){
			return uptimeSeconds + " secs";
		}else if(uptimeSeconds < 3600){
			return uptimeSeconds / 60 + " mins";
		}else if(uptimeSeconds < 86400){
			return uptimeSeconds / 60 / 60 + " hours"; 
		}else{
			return uptimeSeconds / 60 / 60 / 24 + " days";
		}
	}

}
