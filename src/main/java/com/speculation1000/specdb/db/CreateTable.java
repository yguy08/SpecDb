package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;

public class CreateTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public CreateTable(DbConnectionEnum dbce) throws SpecDbException {
		try{
			createMarketTable(dbce);
			createAccountTable(dbce);
			createEntryTable(dbce);
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}
	
	public static void createMarketTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "Base character NOT NULL,\n"
                + "Counter character NOT NULL,\n"
                + "Exchange character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createAccountTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS account (\n"
                + "Date long NOT NULL,\n"
                + "Balance decimal NOT NULL\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createEntryTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS entry (\n"
                + "Base character NOT NULL,\n"
                + "Counter character NOT NULL,\n"
                + "Exchange character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + "Close decimal NOT NULL,\n"
                + "Volume decimal NOT NULL,\n"
                + "ATR decimal NOT NULL,\n"
                + "Amount decimal NOT NULL,\n"
                + "Total decimal NOT NULL,\n"
                + "Direction character NOT NULL,\n"
                + "Stop decimal NOT NULL,\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}

}
