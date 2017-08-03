package utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SpecDbDate {
	
	public static long dateToUtcMidnightSeconds(Date date){
		String dateStr = Long.toString(date.getTime());
		if(dateStr.length() < 11){
			date = new Date(Long.parseLong(date.getTime() + "000"));
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		LocalDateTime closeTime = LocalDateTime.of(year, month, day, 0, 0, 0);
		ZoneId closeZone = ZoneId.of("Etc/UTC"); 
		ZonedDateTime utcMidnight = ZonedDateTime.of(closeTime, closeZone).plusDays(1);
		return utcMidnight.toEpochSecond();
	}
	
	public static long nextHourInitialDelay(){
		LocalTime localTime = LocalTime.now();
		LocalTime nextHour = localTime.withHour(localTime.getHour()).withMinute(59).withSecond(0).withNano(0);
        Duration duration = Duration.between(localTime, nextHour);
        long initalDelay = duration.getSeconds();
		return initalDelay;
	}
	
	public static boolean isNewDay(){
		Instant instant = Instant.now();
		ZoneId z = ZoneId.of( "Etc/UTC" );
		ZonedDateTime zdt = instant.atZone(z);
		int hour = zdt.getHour();
		if(hour == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static long getTodayUtcEpochSeconds(){
		ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Etc/UTC"));
		ZonedDateTime today = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("Etc/UTC"));
		return today.toEpochSecond();
	}
	
	public static void main(String[]args){
		System.out.println(getTodayUtcEpochSeconds());
	}

}
