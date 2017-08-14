package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.log.SpecDbLogger;

public class DbUtils {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();	
	
	public static void nextDayCleanUp(String exchange) throws SpecDbException{
		long yesterday = SpecDbDate.getYesterdayEpochSeconds(StartRun.getStartRunTS());
		long today = SpecDbDate.getTodayMidnightEpochSeconds(StartRun.getStartRunTS());
		Connection connection = DbConnection.connect(DbConnectionEnum.SQLITE_MAIN);
		nextDayCleanUp(connection,exchange,yesterday,today);
		
		try{
			connection.close();
		}catch(SQLException e){
			
		}
	}
	
	public static void nextDayCleanUp(Connection connection, String exchange, long yesterday,long today) throws SpecDbException{
		String sqlDelete = "DELETE FROM Markets WHERE date > " + yesterday + " "
				+ "AND date < (SELECT Max(Date) FROM markets WHERE date > " + yesterday + " "
				+ "AND date < " + today + " "
				+ "AND Exchange = " + "'"+exchange+"'"+")"
				+ "AND Exchange = " + "'"+exchange+"'";
		
		try{
			int deleted = DeleteRecord.deleteRecords(connection,sqlDelete);
			specLogger.logp(Level.INFO, DbUtils.class.getName(),"nextDayCleanUp", "Next day clean up deleted: " 
			+ deleted + " records.");
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
		
		String sqlUpdate = "UPDATE markets SET date = " + yesterday + " " 
				+ "WHERE date > " + yesterday + " "
				+ "AND date < " + today + " "
				+ "AND Exchange = " + "'"+exchange+"'";
		try{
			int updated = UpdateRecord.updateRecords(connection,sqlUpdate);
			specLogger.logp(Level.INFO, DbUtils.class.getName(),"nextDayCleanUp", "Next day clean up updated: " 
			+ updated + " records.");
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}
	
	

}
