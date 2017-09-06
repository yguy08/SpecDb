package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;

public class DropTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final String DROP_TABLE = DropTable.class.getName();
	
	public static void dropMarketTable(DbConnectionEnum dbce){
		String sql = "DROP TABLE IF EXISTS market";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(sql);
            specLogger.logp(Level.INFO, DROP_TABLE, "dropTable", "Table dropped!");
            tmpStatement.close();
            connection.close();
        } catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}

}
