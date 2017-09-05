package com.speculation1000.specdb.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.trade.SpecDbTrade;

public class InsertRecord {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void insertBatchMarkets(Connection connection, List<Market> marketList){
		String sqlCommand = "INSERT INTO markets(Base,Counter,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < marketList.size();i++){
        		Market m = marketList.get(i);
        		tmpStatement.setString(1, m.getBase());
        		tmpStatement.setString(2, m.getCounter());
        		tmpStatement.setString(3, m.getExchange());
        		tmpStatement.setLong(4,m.getDate());
        		tmpStatement.setBigDecimal(5, m.getHigh());
        		tmpStatement.setBigDecimal(6, m.getLow());
        		tmpStatement.setBigDecimal(7,m.getOpen());
        		tmpStatement.setBigDecimal(8, m.getClose());
        		tmpStatement.setInt(9,m.getVolume());
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
	
	public static void insertBatchMarkets(DbConnectionEnum dbce, List<Market> marketList){
		String sqlCommand = "INSERT INTO markets(Base,Counter,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?,?)";
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
        		tmpStatement.setBigDecimal(7,m.getOpen());
        		tmpStatement.setBigDecimal(8, m.getClose());
        		tmpStatement.setInt(9,m.getVolume());
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
	
	public static void insertUpdatedTrades(DbConnectionEnum dbce,List<SpecDbTrade> tradeList){
		String sqlCommand = "INSERT INTO trade(Base,Counter,Exchange,Date,Price,Amount,Total,Stop,CurrentPrice,isOpen) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
        	Connection connection = DbConnection.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < tradeList.size();i++){
        		SpecDbTrade sbt = tradeList.get(i);
        		tmpStatement.setString(1, sbt.getBase());
        		tmpStatement.setString(2, sbt.getCounter());
        		tmpStatement.setString(3, sbt.getExchange());
        		tmpStatement.setLong(4,sbt.getDate());
        		tmpStatement.setBigDecimal(5, sbt.getPrice());
        		tmpStatement.setBigDecimal(6, sbt.getAmount());
        		tmpStatement.setBigDecimal(7,sbt.getTotal());
        		tmpStatement.setBigDecimal(8, sbt.getStop());
        		tmpStatement.setBigDecimal(9,sbt.getCurrentPrice());
        		tmpStatement.setBoolean(10,sbt.isOpen());
        		tmpStatement.addBatch();
        	if((i % 10000 == 0 && i != 0) || i == tradeList.size() - 1){
        		specLogger.logp(Level.INFO, InsertRecord.class.getName(),"insertUpdatedTrades", "Adding batch: " + i);
        		long start = System.currentTimeMillis();
        		tmpStatement.executeBatch();
    	        long end = System.currentTimeMillis();
    	        specLogger.logp(Level.INFO, InsertRecord.class.getName(),"insertUpdatedTrades", "total time taken to insert the batch = " + (end - start) + " ms");
        	}
        }
            tmpStatement.close();
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
