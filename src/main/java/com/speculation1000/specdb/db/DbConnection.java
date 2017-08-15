package com.speculation1000.specdb.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;

public class DbConnection {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final String USER_HOME = System.getProperty("user.home") + "/SpecDb/db/";
	
	static{
		try {
			Files.createDirectories(Paths.get(USER_HOME));
			specLogger.logp(Level.INFO, DbConnection.class.getName(), "static", "Db directory created/exits");
		} catch (IOException e1) {
			specLogger.logp(Level.INFO, DbConnection.class.getName(), "static", "Error creating Db directory " + e1.getMessage());
		}
	}
	
	public static Connection connect(DbConnectionEnum dbce){
		Connection conn = null;
        try {
        	Class.forName(dbce.getClassForName());
            conn = DriverManager.getConnection(dbce.getConnectionString());
            specLogger.logp(Level.INFO, DbConnection.class.getName(), "connect", "Connection to " + dbce.getConnectionString() + " established");
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbConnection.class.getName(), "connect", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        } catch (ClassNotFoundException e) {
        	specLogger.logp(Level.INFO, DbConnection.class.getName(), "connect", "ClassNotFoundException: " + e.getMessage());
		}
        return conn;
	}

}
