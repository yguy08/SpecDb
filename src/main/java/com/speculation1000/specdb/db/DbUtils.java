package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.IntStream;

import org.h2.tools.Csv;

import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Entry;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.Config;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;

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
                + "Symbol character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + "High decimal,\n"
                + "Low decimal,\n"
                + "Close decimal,\n"
                + "Volume int,\n"
                + "H_L int,\n"
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
                + "Symbol character NOT NULL,\n"
                + "Date long NOT NULL,\n"
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
                + "Symbol character NOT NULL,\n"
                + "Date long NOT NULL,\n"
                + "Close decimal NOT NULL,\n"
                + "Volume decimal NOT NULL,\n"
                + "ATR decimal NOT NULL,\n"
                + "Amount decimal NOT NULL,\n"
                + "Total decimal NOT NULL,\n"
                + "Direction character NOT NULL,\n"
                + "Stop decimal NOT NULL,\n"
                + "H_L int NOT NULL,\n"
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
                + "Symbol character NOT NULL,\n"
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
		String strSql = "CREATE INDEX IF NOT EXISTS IDXCLOSE on MARKETS (Symbol,Date,Close)";
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
		String sqlCommand = "SELECT m.SYMBOL,m.DATE,m.CLOSE" 
					  + " FROM MARKETS AS m "
					  + " JOIN (SELECT SYMBOL,Max(Close) Close"
					  + " FROM MARKETS"
					  + " WHERE DATE >= " + fromDateMidnight
					  + " GROUP BY SYMBOL) AS t"
					  + " ON m.SYMBOL = t.SYMBOL"
					  + " AND m.CLOSE >= t.CLOSE"
					  + " WHERE DATE = (SELECT Max(DATE) FROM MARKETS)";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Market> marketList = new ArrayList<>();
            while(resultSet.next()){
            	Symbol s = new Symbol(resultSet.getString(1));
            	Market m = new Market(s,resultSet.getLong(2),
			              resultSet.getBigDecimal(3));
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
		String sqlCommand = "SELECT m.SYMBOL,m.DATE,m.CLOSE" 
					  + " FROM MARKETS AS m "
					  + " JOIN (SELECT SYMBOL,Min(Close) Close"
					  + " FROM MARKETS"
					  + " WHERE DATE >= " + fromDateMidnight
					  + " GROUP BY SYMBOL) AS t"
					  + " ON m.SYMBOL = t.SYMBOL"
					  + " AND m.CLOSE <= t.CLOSE"
					  + " WHERE DATE = (SELECT Max(DATE) FROM MARKETS)";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Market> marketList = new ArrayList<>();
            while(resultSet.next()){
            	Symbol s = new Symbol(resultSet.getString(1));
            	Market m = new Market(s,resultSet.getLong(2),
            			              resultSet.getBigDecimal(3));
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
	
	public static List<Entry> getNewEntries(DbConnectionEnum dbce,int days){
		long fromDateMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT SYMBOL,Date,Close,Volume,ATR,Amount,Total,Direction,Stop,H_L" + 
				" FROM entry WHERE DATE >= " + fromDateMidnight
				+ " ORDER BY DATE DESC";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Entry> entryList = new ArrayList<>();
            while(resultSet.next()){
            	Entry e = new Entry();
            	e.setSymbol(new Symbol(resultSet.getString(1)));
            	e.setDate(resultSet.getLong(2));
            	e.setClose(resultSet.getBigDecimal(3));
            	e.setVolumeFromDb(resultSet.getInt(4));
            	e.setATR(resultSet.getBigDecimal(5));
            	e.setAmount(resultSet.getBigDecimal(6));
            	e.setTotal(resultSet.getBigDecimal(7));
            	e.setDirection(resultSet.getString(8));
            	e.setStop(resultSet.getBigDecimal(9));
            	e.setHighLow(resultSet.getInt(10));
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
		String sqlCommand = "SELECT SYMBOL,DATE,AMOUNT"
						  + " FROM ACCOUNT_BAL WHERE DATE = (SELECT Max(DATE) AS DATE FROM ACCOUNT_BAL)";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<AccountBalance> balanceList = new ArrayList<>();
            AccountBalance ab;
            while(resultSet.next()){
            	Symbol s = new Symbol(resultSet.getString(1));
            	ab = new AccountBalance(s,resultSet.getLong(2),resultSet.getBigDecimal(3));
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
	
	public static List<Long> getDistinctDates(DbConnectionEnum dbce, int days, String exchange) throws SpecDbException {
		long fromDateMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT DISTINCT(DATE) FROM MARKETS WHERE DATE >= " + fromDateMidnight
						  + " ORDER BY DATE ASC";
        try {
        	Connection conn = DbUtils.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            List<Long> dateList = new ArrayList<>();
            while(resultSet.next()){
            	dateList.add(resultSet.getLong(1));
            }
            tmpStatement.close();
            conn.close();
            return dateList;
        }catch(SQLException ex){
        	while (ex != null) {
            	specLogger.logp(Level.INFO, DbUtils.class.getName(), "getDistinctDates", ex.getMessage());
	            ex = ex.getNextException();
	        }
        	throw new SpecDbException("Error getting distinct dates");       	
        }		
	}
	
	public static List<Market> genericMarketQuery(DbConnectionEnum dbce, String sqlCommand){
	    try {
	    	Connection conn = connect(dbce);
	        Statement tmpStatement = conn.createStatement();
	        ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
	        ResultSetMetaData rsmd = resultSet.getMetaData();
	        int i = rsmd.getColumnCount();
	        List<Market> marketList = new ArrayList<>();
	        while(resultSet.next()){
	        	Market market = new Market();
	        	for(int z = 1; z <= i;z++){
	        		String col_name = rsmd.getColumnName(z).toUpperCase();
	        		switch(col_name){
	        		case "SYMBOL":
	        			market.setSymbol(new Symbol(resultSet.getString(z)));
	        			break;
	        		case "DATE":
	        			market.setDate(resultSet.getLong(z));
	        			break;
	        		case "HIGH":
	        			market.setHigh(resultSet.getBigDecimal(z));
	        			break;
	        		case "LOW":
	        			market.setLow(resultSet.getBigDecimal(z));
	        			break;
	        		case "CLOSE":
	        			market.setClose(resultSet.getBigDecimal(z));
	        			break;
	        		case "VOLUME":
	        			market.setVolume(resultSet.getInt(z));
	        			break;
	        		default:
	        			break;
	        		}
	        	}
	        	marketList.add(market);
	        }
	        tmpStatement.close();
	        conn.close();
	        return marketList;
	    } catch (SQLException ex) {
	    	while (ex != null) {
	        	specLogger.logp(Level.INFO, DbUtils.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
	    }
	}
	
	
	/* ---------- END SELECT QUERIES ------------- */
	
	/* ---------- START CLEAN UPS (DELETES) ------------- */

	public static int[] marketCleanUp(long date){
		Connection conn = DbUtils.connect(Config.getDatabase());
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

	public static int insertMarkets(DbConnectionEnum dbce, List<Market> marketList){
		int sum = 0;
		String sqlCommand = "INSERT INTO markets(SYMBOL,Date,High,Low,Close,Volume) VALUES(?,?,?,?,?,?)";
	    try {
	    	Connection connection = DbUtils.connect(dbce);
	        PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < marketList.size();i++){
	    		Market m = marketList.get(i);
	    		tmpStatement.setString(1, m.getSymbol().toString());
	    		tmpStatement.setLong(2,m.getDate());
	    		tmpStatement.setBigDecimal(3, m.getHigh());
	    		tmpStatement.setBigDecimal(4, m.getLow());
	    		tmpStatement.setBigDecimal(5, m.getClose());
	    		tmpStatement.setInt(6,m.getVolume());
	    		tmpStatement.addBatch();
	    	if((i % 10000 == 0 && i != 0) || i == marketList.size() - 1){ 
	    		specLogger.logp(Level.INFO, DbUtils.class.getName(),"insertMarkets", "Adding batch: " + i);
	    		long start = System.currentTimeMillis();
	    		int[] added = tmpStatement.executeBatch();
		        long end = System.currentTimeMillis();
		        specLogger.logp(Level.INFO, DbUtils.class.getName(),"insertMarkets", "total time taken to insert the batch = " + (end - start) + " ms");
		        sum+=IntStream.of(added).sum();
	    	}
	    }
	        tmpStatement.close();
	        connection.close();
	        return sum;
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
		String sqlCommand = "INSERT INTO ACCOUNT_BAL(SYMBOL,DATE,AMOUNT) "
							+ "VALUES(?,?,?)";
        try {
        	Connection connection = DbUtils.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < balanceList.size();i++){
        		AccountBalance ab = balanceList.get(i);
        		tmpStatement.setString(1, ab.getSymbol().toString());
        		tmpStatement.setLong(2, ab.getDate());
        		tmpStatement.setBigDecimal(3, ab.getAmount());
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
		String sqlCommand = "INSERT INTO entry(Symbol,Date,Close,Volume,ATR,Amount,Total,Direction,Stop,H_L) "
							+ "VALUES(?,?,?,?,?,?,?,?,?)";
        try {
        	Connection connection = DbUtils.connect(dbce);
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < entriesList.size();i++){
        		Entry e = entriesList.get(i);
        		tmpStatement.setString(1, e.getSymbol().toString());
        		tmpStatement.setLong(2, SpecDbDate.getLastSixHourSeconds(StandardMode.getStartRunTS()));
        		tmpStatement.setBigDecimal(3, e.getClose());
        		tmpStatement.setInt(4, e.getVolume());
        		tmpStatement.setBigDecimal(5, e.getATR());
        		tmpStatement.setBigDecimal(6, e.getAmount());
        		tmpStatement.setBigDecimal(7, e.getTotal());
        		tmpStatement.setString(8, e.getDirection());
        		tmpStatement.setBigDecimal(9, e.getStop());
        		tmpStatement.setInt(10, e.getHighLow());
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
	
	/* ---- PROTO ---- */
	
	public static void loadMarketsFromCsv(String fileName) throws SQLException{
        ResultSet rs = new Csv().read(fileName, null, null);
        List<Market> markets = new ArrayList<>();
        while (rs.next()) {
            	Symbol s = new Symbol(rs.getString(1),rs.getString(2),rs.getString(3));
            	Market m = new Market(s,rs.getLong(4),rs.getBigDecimal(5),rs.getBigDecimal(6),
            			rs.getBigDecimal(8),rs.getInt(9));
            	markets.add(m);
        }
        rs.close();
        
        DbUtils.insertMarkets(DbConnectionEnum.H2_MAIN, markets);
        
	}

}
