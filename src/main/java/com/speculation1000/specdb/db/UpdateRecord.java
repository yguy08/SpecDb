package com.speculation1000.specdb.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;

public class UpdateRecord {
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
    public static int updateRecords(String strSql) throws SpecDbException {
    	Connection connection = connect();
        try {
            Statement tmpStatement = connection.createStatement();
            int result = tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
            specLogger.logp(Level.INFO, UpdateRecord.class.getName(), "updateRecords", "Records updated..");
            return result;
        } catch (java.sql.SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
    }
    
    public static int updateRecords(Connection connection, String strSql) throws SpecDbException {
        try {
            Statement tmpStatement = connection.createStatement();
            int result = tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            specLogger.logp(Level.INFO, UpdateRecord.class.getName(), "updateRecords", "Records updated..");
            return result;
        } catch (java.sql.SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
    }

    public static Connection connect(){
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
            specLogger.logp(Level.INFO, DbUtils.class.getName(), "connect", "Connection to db established");
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
