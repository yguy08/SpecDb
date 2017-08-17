package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class DeleteRecord {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static int deleteRecords(Connection connection, String strSql){
        try {
            Statement tmpStatement = connection.createStatement();
            int result = tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            return result;
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DeleteRecord.class.getName(), "deleteRecords", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static int[] deleteBulkMarkets(DbConnectionEnum dbce, List<Market> marketList){
		Connection conn = DbConnection.connect(dbce);
		try{
			String deleteSql = "DELETE FROM markets WHERE Date = ?";              
			PreparedStatement st = conn.prepareStatement(deleteSql);
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
