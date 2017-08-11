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
	
	public static Connection mainConnect(){
    	String path = System.getProperty("user.home") + "/SpecDb/db/";
    	try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
        String url = "jdbc:sqlite:" +path+ "Speculation1000.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            specLogger.logp(Level.INFO, DbConnection.class.getName(), "connect", "Connection to db established");
        } catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
        return conn;
	}
	
	public static Connection testConnect(){
    	String path = System.getProperty("user.home") + "/SpecDb/db/";
    	try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
        String url = "jdbc:sqlite:" +path+ "Speculation1000-tmp.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            specLogger.logp(Level.INFO, DbConnection.class.getName(), "testConnect", "Test connection established");
        } catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
        return conn;
	}

}
