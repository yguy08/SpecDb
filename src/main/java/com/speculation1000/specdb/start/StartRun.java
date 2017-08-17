package com.speculation1000.specdb.start;

import java.time.Instant;

import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.time.SpecDbTime;

public class StartRun {
	
	private static Instant startRunTS = null;
	
	public static void setStartRunTS(){
		startRunTS = Instant.now();
	}
	
	public static Instant getStartRunTS(){
		//For testing purposes, don't need to set start every time
		if(startRunTS != null){
			return startRunTS;
		}else{
			setStartRunTS();
			return startRunTS;
		}
	}
	
	public static String getStartRunMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [" + StartApp.mode.getModeName() + "] \n");
        sb.append("********************************\n");
        sb.append("            [START]\n");
        sb.append("* At: ");
        sb.append(SpecDbDate.instantToLogStringFormat(StartRun.getStartRunTS()) + "\n");
        sb.append("* New Day: " + SpecDbDate.isNewDay(StartRun.getStartRunTS()) + "\n");
        sb.append("********************************\n");
        return sb.toString();
	}
	
	public static String getEndRunMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [" + StartApp.mode.getModeName() + "] \n");
        Instant end = Instant.now();
        sb.append("********************************\n");
        sb.append("             [END]\n");
        sb.append("* At: ");
        sb.append(SpecDbDate.instantToLogStringFormat(end) + "\n");
        sb.append("* Runtime: ");
        sb.append(end.getEpochSecond() - StartRun.getStartRunTS().getEpochSecond() + " sec\n");
        sb.append("********************************\n");
        long i = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
        sb.append("* Next Update in " + i + " seconds\n");
        sb.append("********************************\n");
        return sb.toString();
	}

}
