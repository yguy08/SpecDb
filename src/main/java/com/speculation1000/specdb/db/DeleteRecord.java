package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;

public class DeleteRecord {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static int deleteRecords(String strSql){
		Connection connection = DbConnection.mainConnect();
        int deleted = deleteRecords(connection,strSql);
        
        try{
        	connection.close();
        }catch(SQLException e){
        	specLogger.logp(Level.SEVERE, DeleteRecord.class.getName(), "deleteRecords", "Error deleting records!");
        }
        
        return deleted;
	}
	
	public static int deleteRecords(Connection connection, String strSql){
        try {
            Statement tmpStatement = connection.createStatement();
            int result = tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
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


}
