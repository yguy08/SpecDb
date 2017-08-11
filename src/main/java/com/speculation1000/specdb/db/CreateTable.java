package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;

public class CreateTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void createTable(){
		Connection connection = DbConnection.mainConnect();
		createTable(connection);
		try{
			connection.close();
		}catch(SQLException e){
			
		}
	}
	
	public static void createTable(Connection connection){
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Exchange character NOT NULL,\n"
                + "	Date long NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", "Table created");
            tmpStatement.close();
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
