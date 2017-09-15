package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;

public class DbUtils {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	/* ---------- START CONNECT ------------- */
	
	public static Connection connect(DbConnectionEnum dbce){
		Connection conn = null;
        try {
        	Class.forName(dbce.getClassForName());
            conn = DriverManager.getConnection(dbce.getConnectionString());
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.SEVERE, DbConnection.class.getName(), "connect", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        } catch (ClassNotFoundException e) {
        	specLogger.logp(Level.SEVERE, DbConnection.class.getName(), "connect", e.getMessage());
		}
        return conn;
	}
	
	/* ---------- END CONNECT ------------- */
	
	/* ---------- START CREATE TABLES ------------- */
	
	public static void createMarketTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "Base character NOT NULL,\n"
                + "Counter character NOT NULL,\n"
                + "Exchange character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createAccountTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS account (\n"
                + "Date long NOT NULL,\n"
                + "Balance decimal NOT NULL\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createEntryTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS entry (\n"
                + "Base character NOT NULL,\n"
                + "Counter character NOT NULL,\n"
                + "Exchange character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + "Close decimal NOT NULL,\n"
                + "Volume decimal NOT NULL,\n"
                + "ATR decimal NOT NULL,\n"
                + "Amount decimal NOT NULL,\n"
                + "Total decimal NOT NULL,\n"
                + "Direction character NOT NULL,\n"
                + "Stop decimal NOT NULL,\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createTradeTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS trade (\n"
                + "Base character NOT NULL,\n"
                + "Counter character NOT NULL,\n"
                + "Exchange character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + "Close decimal NOT NULL,\n"
                + "Volume decimal NOT NULL,\n"
                + "ATR decimal NOT NULL,\n"
                + "Amount decimal NOT NULL,\n"
                + "Total decimal NOT NULL,\n"
                + "Direction character NOT NULL,\n"
                + "Stop decimal NOT NULL,\n"
                + "Status character NOT NULL\n"
                + ");";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, CreateTable.class.getName(), "createTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	/* ---------- END CREATE TABLES ------------- */
	
	/* ---------- START CREATE INDEX ------------- */
	
	public static void createCloseIndex(DbConnectionEnum dbce) throws SpecDbException {
		String strSql = "CREATE INDEX IF NOT EXISTS IDXCLOSE on MARKETS (Base,Counter,Exchange,Date,Close)";
        try {
        	Connection connection = DbConnection.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
            specLogger.logp(Level.INFO, DbUtils.class.getName(), "createCloseIndex", "Close index created (if it didn't already exist)");
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.SEVERE, DbUtils.class.getName(), "createCloseIndex", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new SpecDbException("Error creating close index!");
        }		
	}
	
	/* ---------- END CREATE INDEX ------------- */
	
	/* ---------- START SELECT QUERIES ------------- */
	
	public static List<Market> getMarketHighs(DbConnectionEnum dbce,int days){
		long fromDateMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT m.BASE,m.COUNTER,m.EXCHANGE,m.DATE,m.CLOSE" 
					  + " FROM MARKETS AS m "
					  + " JOIN (SELECT BASE,COUNTER,EXCHANGE,Max(Close) Close"
					  + " FROM MARKETS"
					  + " WHERE DATE >= " + fromDateMidnight
					  + " GROUP BY BASE,COUNTER,EXCHANGE) AS t"
					  + " ON m.BASE = t.BASE"
					  + " AND m.COUNTER = t.COUNTER"
					  + " AND m.CLOSE >= t.CLOSE"
					  + " WHERE DATE = (SELECT Max(DATE) FROM MARKETS)";
        try {
        	Connection conn = DbConnection.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Market> marketList = new ArrayList<>();
            while(resultSet.next()){
            	Market m = new Market(resultSet.getString(1),resultSet.getString(2),
            			              resultSet.getString(3));
            	marketList.add(m);
            }
            tmpStatement.close();
            conn.close();
            return marketList;
        }catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getMarketHighs", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");        	
        }		
	}
	
	public static List<Market> getMarketLows(DbConnectionEnum dbce,int days){
		long fromDateMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT m.BASE,m.COUNTER,m.EXCHANGE,m.DATE,m.CLOSE" 
					  + " FROM MARKETS AS m "
					  + " JOIN (SELECT BASE,COUNTER,EXCHANGE,Min(Close) Close"
					  + " FROM MARKETS"
					  + " WHERE DATE >= " + fromDateMidnight
					  + " GROUP BY BASE,COUNTER,EXCHANGE) AS t"
					  + " ON m.BASE = t.BASE"
					  + " AND m.COUNTER = t.COUNTER"
					  + " AND m.CLOSE <= t.CLOSE"
					  + " WHERE DATE = (SELECT Max(DATE) FROM MARKETS)";
        try {
        	Connection conn = DbConnection.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Market> marketList = new ArrayList<>();
            while(resultSet.next()){
            	Market m = new Market(resultSet.getString(1),resultSet.getString(2),
            			              resultSet.getString(3));
            	marketList.add(m);
            }
            tmpStatement.close();
            conn.close();
            return marketList;
        }catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getMarketHighs", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");        	
        }		
	}
	
	public static List<Market> getNewTrades(DbConnectionEnum dbce){
		String sqlCommand = "SELECT Base,Counter,Exchange,Date,Close,Volume,ATR,Amount,Total,Direction,Stop,Status"
						  + " FROM trade WHERE STATUS = 'NEW' GROUP BY Base,Counter,Exchange";
        try {
        	Connection conn = DbConnection.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Market> marketList = new ArrayList<>();
            while(resultSet.next()){
            	Market m = new Market();
            	m.setBase(resultSet.getString(1));
            	m.setCounter(resultSet.getString(2));
            	m.setExchange(resultSet.getString(3));
            	m.setDate(resultSet.getLong(4));
            	m.setClose(resultSet.getBigDecimal(5));
            	m.setVolume(resultSet.getInt(6));
            	m.setATR(resultSet.getBigDecimal(7));
            	m.setAmount(resultSet.getBigDecimal(8));
            	m.setTotal(resultSet.getBigDecimal(9));
            	m.setDirection(resultSet.getString(10));
            	m.setStop(resultSet.getBigDecimal(11));
            	m.setStatus(resultSet.getString(12));
            	m.setToStr(m.getSymbol()+" @"+m.getClose()+" "+m.getDirection());
            	marketList.add(m);
            }
            tmpStatement.close();
            conn.close();
            return marketList;
        }catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getMarketHighs", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");        	
        }		
	}
	
	/* ---------- END SELECT QUERIES ------------- */
	
	/* ---------- START DELETES ------------- */
	
	public static int[] deleteNewTrades(DbConnectionEnum dbce, long date){
		Connection conn = DbConnection.connect(dbce);
		try{
			String deleteSql = "DELETE FROM trade WHERE Date = ?";              
			PreparedStatement st = conn.prepareStatement(deleteSql);
			st.setLong(1, date);
			st.addBatch();
			int[] results = st.executeBatch();
			st.close();
			conn.close();
			return results;
		}catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DeleteRecord.class.getName(), "deleteEntries", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}		
	}
	
	/* ---------- END DELETES ------------- */
	
	/* ---------- START INSERTS ------------- */
	
	public static void insertNewTrades(DbConnectionEnum dbce, List<Market> newMarketEntriesList) {
		String sqlCommand = "INSERT INTO trade(Base,Counter,Exchange,Date,Close,Volume,ATR,Amount,Total,Direction,Stop,Status) "
							+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
        	Connection connection = DbConnection.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < newMarketEntriesList.size();i++){
        		Market m = newMarketEntriesList.get(i);
        		tmpStatement.setString(1, m.getBase());
        		tmpStatement.setString(2,m.getCounter());
        		tmpStatement.setString(3, m.getExchange());
        		tmpStatement.setLong(4, m.getDate());
        		tmpStatement.setBigDecimal(5, m.getClose());
        		tmpStatement.setInt(6, m.getVolume());
        		tmpStatement.setBigDecimal(7, m.getATR());
        		tmpStatement.setBigDecimal(8, m.getAmount());
        		tmpStatement.setBigDecimal(9, m.getTotal());
        		tmpStatement.setString(10, m.getDirection());
        		tmpStatement.setBigDecimal(11, m.getStop());
        		tmpStatement.setString(12, m.getStatus());
        		tmpStatement.addBatch();
	        }
    		long start = System.currentTimeMillis();
    		tmpStatement.executeBatch();
	        long end = System.currentTimeMillis();
	        specLogger.logp(Level.INFO, InsertRecord.class.getName(),"insertBatchEntries", "total time taken to insert the batch = " + (end - start) + " ms");
    	
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
	
	/* ---------- END INSERTS ------------- */

}
