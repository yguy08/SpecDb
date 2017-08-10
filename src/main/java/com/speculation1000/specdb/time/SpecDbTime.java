package com.speculation1000.specdb.time;

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
	
	
	
	public static void main(String[] args){
		
	}

}
