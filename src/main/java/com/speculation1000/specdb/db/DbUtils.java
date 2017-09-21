package com.speculation1000.specdb.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Entry;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;

public class DbUtils {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	/* ---------- START CONNECT ------------- */
	
	public static Connection connect(DbConnectionEnum dbce) {		
		Connection conn = null;
	    try {
	    	Class.forName(dbce.getClassForName());
	        conn = DriverManager.getConnection(dbce.getConnectionString());
	    } catch (SQLException ex) {
	    	while (ex != null) {
	        	specLogger.logp(Level.SEVERE, DbUtils.class.getName(), "connect", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
	    } catch (ClassNotFoundException e) {
	    	specLogger.logp(Level.SEVERE, DbUtils.class.getName(), "connect", e.getMessage());
		}
	    return conn;
	}	
	
	/* ---------- END CONNECT ------------- */
	
	/* ---------- START CREATE TABLES ------------- */
	
	public static void createTables(DbConnectionEnum dbce) throws SpecDbException {		
		try{
			createMarketTable(dbce);
			createAccountBalTable(dbce);
			createEntryTable(dbce);
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}
	
	private static void createMarketTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS MARKETS (\n"
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
        	Connection connection = DbUtils.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "createMarketTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	private static void createAccountBalTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS ACCOUNT_BAL (\n"
                + "Date long NOT NULL,\n"
                + "Counter character NOT NULL,\n"
				+ "Exchange character NOT NULL,\n"
				+ "Amount character NOT NULL\n"
                + ");";
        try {
        	Connection connection = DbUtils.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "createAccountBalTable", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	private static void createEntryTable(DbConnectionEnum dbce){
		String strSql = "CREATE TABLE IF NOT EXISTS ENTRY (\n"
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
        	Connection connection = DbUtils.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "createEntryTable", ex.getMessage());
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
        	Connection connection = DbUtils.connect(dbce);
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "createTradeTable", ex.getMessage());
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
        	Connection connection = DbUtils.connect(dbce);
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
        	Connection conn = DbUtils.connect(dbce);
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
        	Connection conn = DbUtils.connect(dbce);
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
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getMarketLows", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");        	
        }		
	}
	
	public static List<Entry> getNewEntries(DbConnectionEnum dbce){
		String sqlCommand = "SELECT Base,Counter,Exchange,Date,Close,Volume,ATR,Amount,Total,Direction,Stop"
						  + " FROM entry WHERE DATE = (SELECT Max(DATE) AS Date FROM ENTRY) GROUP BY Base,Counter,Exchange,Direction";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Entry> entryList = new ArrayList<>();
            while(resultSet.next()){
            	Entry e = new Entry();
            	e.setBase(resultSet.getString(1));
            	e.setCounter(resultSet.getString(2));
            	e.setExchange(resultSet.getString(3));
            	e.setDate(resultSet.getLong(4));
            	e.setClose(resultSet.getBigDecimal(5));
            	e.setVolume(resultSet.getInt(6));
            	e.setATR(resultSet.getBigDecimal(7));
            	e.setAmount(resultSet.getBigDecimal(8));
            	e.setTotal(resultSet.getBigDecimal(9));
            	e.setDirection(resultSet.getString(10));
            	e.setStop(resultSet.getBigDecimal(11));
            	entryList.add(e);
            }
            tmpStatement.close();
            conn.close();
            return entryList;
        }catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getNewEntries", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");        	
        }		
	}
	
	public static List<AccountBalance> getLatestAccountBalances(DbConnectionEnum dbce) throws SpecDbException {
		String sqlCommand = "SELECT DATE,COUNTER,EXCHANGE,AMOUNT"
						  + " FROM ACCOUNT_BAL WHERE DATE = (SELECT Max(DATE) AS DATE FROM ACCOUNT_BAL)";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<AccountBalance> balanceList = new ArrayList<>();
            AccountBalance ab;
            while(resultSet.next()){
            	ab = new AccountBalance(resultSet.getLong(1),resultSet.getString(2),
            											resultSet.getString(3),resultSet.getBigDecimal(4));
            	balanceList.add(ab);
            }
            tmpStatement.close();
            conn.close();
            return balanceList;
        }catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getLatestAccountBalances", ex.getMessage());
	            ex = ex.getNextException();
	        }
        	throw new SpecDbException("Error getting latest account balances");       	
        }		
	}
	
	public static TreeMap<Symbol,BigDecimal> getCurrentMarketClose(DbConnectionEnum dbce, List<Symbol> symbolList) throws SpecDbException {
		String sqlCommand = " ";
		try {
			Connection conn = DbUtils.connect(dbce);
			Statement tmpStatement = conn.createStatement();
			ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
			TreeMap<Symbol,BigDecimal> closeMap = new TreeMap<>();
			while(resultSet.next()){
				Symbol symbol = new Symbol(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
				closeMap.put(symbol, resultSet.getBigDecimal(4));
			}
			tmpStatement.close();
			conn.close();
			return closeMap;
		}catch(SQLException ex){
			while (ex != null) {
		  	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getLatestAccountBalances", ex.getMessage());
		      ex = ex.getNextException();
		  }
			throw new SpecDbException("Error getting latest account balances");       	
		}		
		
	}
	
	/* ---------- END SELECT QUERIES ------------- */
	
	/* ---------- START CLEAN UPS (DELETES) ------------- */

	public static int[] marketCleanUp(DbConnectionEnum dbce, long date){
		Connection conn = DbUtils.connect(dbce);
		try{
			String deleteSql = "DELETE FROM markets WHERE Date = ?";              
			PreparedStatement st = conn.prepareStatement(deleteSql);
			st.setLong(1, date);
			st.addBatch();
			int[] results = st.executeBatch();
			st.close();
			conn.close();
			return results;
		}catch(SQLException ex){
	    	while (ex != null) {
	    		specLogger.logp(Level.INFO, DbUtils.class.getName(), "marketCleanUp", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}
	}
	
	public static int[] newEntriesCleanUp(DbConnectionEnum dbce, long date){
		Connection conn = DbUtils.connect(dbce);
		try{
			String deleteSql = "DELETE FROM entry WHERE Date = ?";              
			PreparedStatement st = conn.prepareStatement(deleteSql);
			st.setLong(1, date);
			st.addBatch();
			int[] results = st.executeBatch();
			st.close();
			conn.close();
			return results;
		}catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "newEntriesCleanUp", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}		
	}
	
	public static int[] accountBalCleanUp(DbConnectionEnum dbce,long date) throws SpecDbException{
		Connection conn = DbUtils.connect(dbce);
		try{
			String deleteSql = "DELETE FROM ACCOUNT_BAL WHERE Date = ?";              
			PreparedStatement st = conn.prepareStatement(deleteSql);
			st.setLong(1, date);
			st.addBatch();
			int[] results = st.executeBatch();
			st.close();
			conn.close();
			return results;
		}catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "accountBalCleanUp", "SQLException: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new SpecDbException("Error cleaning up account balances");
		}		
	}
	
	/* ---------- END CLEAN UPS (DELETES) ------------- */
	
	/* ---------- START INSERTS ------------- */

	public static void insertMarkets(DbConnectionEnum dbce, List<Market> marketList){
		String sqlCommand = "INSERT INTO markets(Base,Counter,Exchange,Date,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
	    try {
	    	Connection connection = DbUtils.connect(dbce);
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
	    		specLogger.logp(Level.INFO, DbUtils.class.getName(),"insertMarkets", "Adding batch: " + i);
	    		long start = System.currentTimeMillis();
	    		tmpStatement.executeBatch();
		        long end = System.currentTimeMillis();
		        specLogger.logp(Level.INFO, DbUtils.class.getName(),"insertMarkets", "total time taken to insert the batch = " + (end - start) + " ms");
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
	
	public static void insertUpdatedAccountBalances(DbConnectionEnum dbce, List<AccountBalance> balanceList) {
		String sqlCommand = "INSERT INTO ACCOUNT_BAL(DATE,EXCHANGE,COUNTER,AMOUNT) "
							+ "VALUES(?,?,?,?)";
        try {
        	Connection connection = DbUtils.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < balanceList.size();i++){
        		AccountBalance ab = balanceList.get(i);
        		tmpStatement.setLong(1, ab.getDate());
        		tmpStatement.setString(2, ab.getExchange());
        		tmpStatement.setString(3, ab.getCounter());
        		tmpStatement.setBigDecimal(4, ab.getAmount());
        		tmpStatement.addBatch();
	        }
    		long start = System.currentTimeMillis();
    		tmpStatement.executeBatch();
	        long end = System.currentTimeMillis();
	        specLogger.logp(Level.INFO, DbUtils.class.getName(),"insertUpdatedAccountBalances", "total time taken to insert the batch = " + (end - start) + " ms");
    	
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
	
	public static void insertNewEntries(DbConnectionEnum dbce, List<Entry> entriesList) {
		String sqlCommand = "INSERT INTO entry(Base,Counter,Exchange,Date,Close,Volume,ATR,Amount,Total,Direction,Stop) "
							+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try {
        	Connection connection = DbUtils.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < entriesList.size();i++){
        		Entry e = entriesList.get(i);
        		tmpStatement.setString(1, e.getBase());
        		tmpStatement.setString(2,e.getCounter());
        		tmpStatement.setString(3, e.getExchange());
        		tmpStatement.setLong(4, Instant.now().getEpochSecond());
        		tmpStatement.setBigDecimal(5, e.getClose());
        		tmpStatement.setInt(6, e.getVolume());
        		tmpStatement.setBigDecimal(7, e.getATR());
        		tmpStatement.setBigDecimal(8, e.getAmount());
        		tmpStatement.setBigDecimal(9, e.getTotal());
        		tmpStatement.setString(10, e.getDirection());
        		tmpStatement.setBigDecimal(11, e.getStop());
        		tmpStatement.addBatch();
	        }
    		long start = System.currentTimeMillis();
    		tmpStatement.executeBatch();
	        long end = System.currentTimeMillis();
	        specLogger.logp(Level.INFO, DbUtils.class.getName(),"insertNewEntries", "total time taken to insert the batch = " + (end - start) + " ms");
    	
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
