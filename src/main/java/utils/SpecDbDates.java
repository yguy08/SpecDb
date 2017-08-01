package utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SpecDbDates {
	
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
	
	public static void main(String[]args){
		System.out.println(dateToUtcMidnightSeconds(new Date()));
	}

}
