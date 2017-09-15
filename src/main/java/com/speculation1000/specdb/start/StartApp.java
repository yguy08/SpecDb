package com.speculation1000.specdb.start;

import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Level;

import com.speculation1000.specdb.db.CreateTable;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.time.SpecDbDate;

public class StartApp {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant APP_START_UP_TS = Instant.now();
	
	public StartApp(){
		
		startUpStatusMessage();
		
		try {
			DbServer.startDB();
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "H2 server start up successful");
		} catch (SQLException e) {
			specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", "Unable to start H2 server: " + e.getMessage());
		}
		
		try{
			//check db connection
			DbConnectionEnum dbce = DbConnectionEnum.H2_MAIN;
			//create market, account and trade table (if the don't already exist)
			new CreateTable(dbce);
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Able to connect to db and create tables.");
			//create close index
			DbUtils.createCloseIndex(dbce);
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Close index created");
		}catch(SpecDbException e){
			specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", "Unable to connect to H2 server. Creating tables failed\n" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new StartApp();
		Config config = new Config();
		new StandardMode(config).startRun();
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
