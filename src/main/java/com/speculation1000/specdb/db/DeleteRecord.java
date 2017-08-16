package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;

public class DeleteRecord {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static int deleteRecords(Connection connection, String strSql){
        try {
            Statement tmpStatement = connection.createStatement();
            int result = tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            return result;
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DeleteRecord.class.getName(), "deleteRecords", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}


}
