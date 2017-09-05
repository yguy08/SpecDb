package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;

public class CreateTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void createTable(Connection connection){
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "Base character NOT NULL,\n"
                + "Counter character NOT NULL,\n"
                + "Exchange character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createAccountTable(Connection connection){
		String strSql = "CREATE TABLE IF NOT EXISTS account (\n"
                + "Date long NOT NULL,\n"
                + "Balance decimal NOT NULL\n"
                + ");";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}

}
