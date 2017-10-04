package com.speculation1000.specdb.start;

import java.time.Instant;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;

public class StartApp {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant APP_START_UP_TS = Instant.now();
	
	public StartApp() throws Exception{		
		try{
			DbConnectionEnum dbce = Config.getDatabase();
			startUpStatusMessage();
			DbServer.startDB();
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Db start up successful: "+dbce);
			DbUtils.createTables(dbce);
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Able to connect to db and create tables.");
			DbUtils.createCloseIndex(dbce);
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Close index created");
		}catch(Exception e){
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Failed to start up. EXIT.");
			throw e;
		}
	}

	public static void main(String[] args) {
		try{
			Config.configSetUp();
			new StartApp();
			new StandardMode();
		}catch(Throwable t){
			for(StackTraceElement ste : t.getStackTrace()){
				specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", ste.toString());
			}
			specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", "ERROR\nShutting Down...");
			System.exit(1);
		}
	}

	public static Instant getStartUpTs() {
		return APP_START_UP_TS;
	}
	
	/**
	 * @returns seconds of system up time
	 */
	public static long getSystemUptime(){
		return Instant.now().getEpochSecond() - StartApp.getStartUpTs().getEpochSecond();
	}
	
	public static void startUpStatusMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********** SpecDb ************** \n");
		sb.append("[ Digital Asset Price Database ]\n");
		sb.append("********************************\n");
		sb.append("* Start Up: \n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartUpTs()) + "\n");
		sb.append("********************************\n");		
		specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", sb.toString());
	}

}
