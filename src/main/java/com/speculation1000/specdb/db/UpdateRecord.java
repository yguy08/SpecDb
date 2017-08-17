package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;

public class UpdateRecord {
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
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
    
	public static int[] updateBulkMarkets(DbConnectionEnum dbce, List<Market> marketList){
		Connection conn = DbConnection.connect(dbce);
		try{
			String sqlUpdate = "UPDATE markets SET Date = ?";              
			PreparedStatement st = conn.prepareStatement(sqlUpdate);
			for(Market m : marketList){
				st.setLong(1, m.getDate());
				st.addBatch();
			}
			int[] results = st.executeBatch();
			st.close();
			conn.close();
			return results;
		}catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DeleteRecord.class.getName(), "deleteRecords", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}
	}
}
