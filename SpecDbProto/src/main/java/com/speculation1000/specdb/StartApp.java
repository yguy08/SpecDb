package com.speculation1000.specdb;

import java.time.Instant;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.mode.Mode;
import com.speculation1000.specdb.mode.ModeFactory;
import com.speculation1000.specdb.mode.QuickMode;
import com.speculation1000.specdb.utils.SpecDbDate;

public class StartApp {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant START_UP_TS = Instant.now();

	public static void main(String[] args) {
		specLogger.logp(Level.INFO, StartApp.class.getName(), "main", StartApp.startUpStatusMessage());
		if(args.length > 0){
			Mode mode = ModeFactory.getMode(args[0]);
			mode.startApp();
		}else{
			new QuickMode().startApp();
		}
	}

	public static Instant getStartUpTs() {
		return START_UP_TS;
	}
	
	/*
	 * @returns seconds of system uptime
	 */
	public static long getSystemUptime(){
		return Instant.now().getEpochSecond() - StartApp.getStartUpTs().getEpochSecond();
	}
	
	public static String startUpStatusMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********** SpecDb ************** \n");
		sb.append("[ Digital Asset Price Database ]\n");
		sb.append("********************************\n");
		sb.append("* Start Up: \n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartUpTs()) + "\n");
		sb.append("********************************\n");
		return sb.toString();
	}

}
