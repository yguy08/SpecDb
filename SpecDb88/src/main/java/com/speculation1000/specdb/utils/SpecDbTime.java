package com.speculation1000.specdb.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SpecDbTime {
	
	public static int getQuickModeDelay(Instant instant){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Etc/UTC"));
		int quarter = zdt.getMinute();
		ZonedDateTime nextQuarter;
		if(quarter < 15){
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(14).withSecond(0).withNano(0);
		}else if(quarter < 30){
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(29).withSecond(0).withNano(0);
		}else if(quarter < 45){
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(44).withSecond(0).withNano(0);
		}else{
			nextQuarter = zdt.withHour(zdt.getHour()).withMinute(59).withSecond(0).withNano(0);
		}
		Duration duration = Duration.between(zdt, nextQuarter);
        int initalDelay = (int) duration.toMinutes();
		return initalDelay;		
	}
	
	public static void main(String[] args){
		
	}

}
