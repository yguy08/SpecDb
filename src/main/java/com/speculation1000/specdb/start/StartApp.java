package com.speculation1000.specdb.start;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Level;

import com.speculation1000.specdb.db.CreateTable;
import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.mode.Mode;
import com.speculation1000.specdb.mode.ModeFactory;
import com.speculation1000.specdb.mode.StandardMode;
import com.speculation1000.specdb.time.SpecDbDate;

public class StartApp {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant APP_START_UP_TS = Instant.now();
	
	protected static Mode mode;
	
	public StartApp(){
		specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", StartApp.startUpStatusMessage());
		
		try {
			DbServer.startDB();
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "H2 server start up successful");
		} catch (SQLException e) {
			specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", "Unable to start H2 server");
		}
		
		try{
			Connection conn = DbConnection.connect(DbConnectionEnum.H2_MAIN);
			CreateTable.createTable(conn);
			specLogger.logp(Level.INFO, StartApp.class.getName(), "StartApp", "Market table created (if it didn't already exist)");
			conn.close();
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", "Unable to connect to H2 server" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new StartApp();
		if(args.length > 0){
			mode = ModeFactory.getMode(args[0]);
		}else{
			mode = new StandardMode();
		}
		
		mode.startRun();
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
	
	 
    //To install htop simply use
        //sudo apt-get install htop

    //Once installed you can start it using
        //htop

}
