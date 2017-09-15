package com.speculation1000.specdb.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class InsertRecord {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void insertBatchMarkets(DbConnectionEnum dbce, List<Market> marketList){
		String sqlCommand = "INSERT INTO markets(Base,Counter,Exchange,Date,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
        try {
        	Connection connection = DbConnection.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < marketList.size();i++){
        		Market m = marketList.get(i);
        		tmpStatement.setString(1, m.getBase());
        		tmpStatement.setString(2, m.getCounter());
        		tmpStatement.setString(3, m.getExchange());
        		tmpStatement.setLong(4,m.getDate());
        		tmpStatement.setBigDecimal(5, m.getHigh());
        		tmpStatement.setBigDecimal(6, m.getLow());
        		tmpStatement.setBigDecimal(7, m.getClose());
        		tmpStatement.setInt(8,m.getVolume());
        		tmpStatement.addBatch();
        	if((i % 10000 == 0 && i != 0) || i == marketList.size() - 1){
        		specLogger.logp(Level.INFO, InsertRecord.class.getName(),"insertBatchMarkets", "Adding batch: " + i);
        		long start = System.currentTimeMillis();
        		tmpStatement.executeBatch();
    	        long end = System.currentTimeMillis();
    	        specLogger.logp(Level.INFO, InsertRecord.class.getName(),"insertBatchMarkets", "total time taken to insert the batch = " + (end - start) + " ms");
        	}
        }
            tmpStatement.close();
            connection.close();
        } catch (SQLException ex) {
        	StringBuffer sb = new StringBuffer();
	        sb.append("SQLException information\n");
	        while (ex != null) {
	            sb.append("Error msg: " + ex.getMessage() + "\n");
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void insertAccountBalance(DbConnectionEnum dbce, BigDecimal balance, long date){
		String sqlCommand = "INSERT INTO account(Date,Balance) VALUES(?,?)";
        try {
        	Connection connection = DbConnection.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        tmpStatement.setLong(1, date);
	        tmpStatement.setBigDecimal(2, balance);
    		tmpStatement.execute();
	        tmpStatement.close();
	        connection.close();
        } catch (SQLException ex) {
        	StringBuffer sb = new StringBuffer();
	        sb.append("SQLException information\n");
	        while (ex != null) {
	            sb.append("Error msg: " + ex.getMessage() + "\n");
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
}
