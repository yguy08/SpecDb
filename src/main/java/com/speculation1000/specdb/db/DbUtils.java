package com.speculation1000.specdb.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbDate;

public class DbUtils {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void insertBatchMarkets(List<Market> marketList){
		Connection connection = connect();
		String sqlCommand = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < marketList.size();i++){
        		Market m = marketList.get(i);
        		tmpStatement.setString(1, m.getSymbol());
        		tmpStatement.setString(2, m.getExchange());
        		tmpStatement.setLong(3,m.getDate());
        		tmpStatement.setBigDecimal(4, m.getHigh());
        		tmpStatement.setBigDecimal(5, m.getLow());
        		tmpStatement.setBigDecimal(6,m.getOpen());
        		tmpStatement.setBigDecimal(7, m.getClose());
        		tmpStatement.setInt(8,m.getVolume());
        		tmpStatement.addBatch();
        	if((i % 10000 == 0 && i != 0) || i == marketList.size() - 1){
        		specLogger.log(DbUtils.class.getName(), "Adding batch: " + i);
        		long start = System.currentTimeMillis();
        		tmpStatement.executeBatch();
    	        long end = System.currentTimeMillis();
    	        specLogger.log(DbUtils.class.getName(), "total time taken to insert the batch = " + (end - start) + " ms");
        	}
        }
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
        	StringBuffer sb = new StringBuffer();
	        sb.append("SQLException information\n");
	        while (ex != null) {
	            sb.append("Error msg: " + ex.getMessage() + "\n");
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void insertBatchMarkets(Connection connection, List<Market> marketList){
		String sqlCommand = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < marketList.size();i++){
        		Market m = marketList.get(i);
        		tmpStatement.setString(1, m.getSymbol());
        		tmpStatement.setString(2, m.getExchange());
        		tmpStatement.setLong(3,m.getDate());
        		tmpStatement.setBigDecimal(4, m.getHigh());
        		tmpStatement.setBigDecimal(5, m.getLow());
        		tmpStatement.setBigDecimal(6,m.getOpen());
        		tmpStatement.setBigDecimal(7, m.getClose());
        		tmpStatement.setInt(8,m.getVolume());
        		tmpStatement.addBatch();
        	if((i % 10000 == 0 && i != 0) || i == marketList.size() - 1){
        		specLogger.log(DbUtils.class.getName(), "Adding batch: " + i);
        		long start = System.currentTimeMillis();
        		tmpStatement.executeBatch();
    	        long end = System.currentTimeMillis();
    	        specLogger.log(DbUtils.class.getName(), "total time taken to insert the batch = " + (end - start) + " ms");
        	}
        }
            tmpStatement.close();
        } catch (java.sql.SQLException ex) {
        	StringBuffer sb = new StringBuffer();
	        sb.append("SQLException information\n");
	        while (ex != null) {
	            sb.append("Error msg: " + ex.getMessage() + "\n");
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static int deleteRecords(String strSql){
		Connection connection = connect();
		int result = 0;
        try {
            Statement tmpStatement = connection.createStatement();
            result = tmpStatement.executeUpdate(strSql);
            tmpStatement.close();
            connection.close();
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
	
	public static List<Market> genericMarketQuery(String sqlCommand){
		Connection connection = connect();
        try {
            Statement tmpStatement = connection.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int i = rsmd.getColumnCount();
            System.out.println("Total columns: "+rsmd.getColumnCount());  
            System.out.println("Column Name of 1st column: "+rsmd.getColumnName(1));  
            System.out.println("Column Type Name of 1st column: "+rsmd.getColumnTypeName(1));
            List<Market> marketList = new ArrayList<>();
            while(resultSet.next()){
            	Market market = new Market();
            	for(int z = 1; z <= i;z++){
            		String col_name = rsmd.getColumnName(z);
            		switch(col_name){
            		case "Symbol":
            			market.setSymbol(resultSet.getString(z));
            			break;
            		case "Exchange":
            			market.setExchange(resultSet.getString(z));
            			break;
            		case "Date":
            			market.setDate(resultSet.getLong(z));
            			break;
            		case "High":
            			market.setHigh(resultSet.getBigDecimal(z));
            			break;
            		case "Low":
            			market.setLow(resultSet.getBigDecimal(z));
            			break;
            		case "Open":
            			market.setOpen(resultSet.getBigDecimal(z));
            			break;
            		case "Close":
            			market.setClose(resultSet.getBigDecimal(z));
            			break;
            		case "Volume":
            			market.setVolume(resultSet.getInt(z));
            			break;
            		case "ATR":
            			market.setTrueRange(resultSet.getBigDecimal(z));
            			break;
            		default:
            			break;
            		}
            	}
            	marketList.add(market);
            }
            tmpStatement.close();
            connection.close();
            return marketList;
        } catch (java.sql.SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static Connection connect(){
    	String path = System.getProperty("user.home") + "/SpecDb/db/";
    	try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
        String url = "jdbc:sqlite:" +path+ "Speculation1000.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            specLogger.logp(Level.INFO, DbUtils.class.getName(), "connect", "Connection to db established");
        } catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
        return conn;
	}
	
	public static Connection testConnect(){
    	String path = System.getProperty("user.home") + "/SpecDb/db/";
    	try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
        String url = "jdbc:sqlite:" +path+ "Speculation1000-tmp.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            specLogger.logp(Level.INFO, DbUtils.class.getName(), "testConnect", "Test connection established");
        } catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
        return conn;
	}
	
	public void createTable(){
		Connection connection = connect();
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Exchange character NOT NULL,\n"
                + "	Date int NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            specLogger.logp(Level.INFO, DbUtils.class.getName(), "createTable", "Table created/exists!");
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void createTable(Connection connection){
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Exchange character NOT NULL,\n"
                + "	Date int NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            System.out.println("Table created!");
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
	
	public void dropTable(){
		Connection connection = connect();
		String sql = "DROP TABLE markets";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(sql);
            System.out.println("Table dropped!");
            tmpStatement.close();
            connection.close();
        } catch (java.sql.SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static void dropTable(Connection connection){
		String sql = "DROP TABLE IF EXISTS markets";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(sql);
            System.out.println("Table dropped!");
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
	
	public static int nextDayCleanUp(Instant startRunTS, String exchange) throws SpecDbException{
		long yesterday = SpecDbDate.getYesterdayEpochSeconds(SpecDbDate.getTodayMidnightInstant(startRunTS));
		long today = SpecDbDate.getTodayUtcEpochSeconds(startRunTS);
		
		String sqlDelete = "DELETE FROM Markets WHERE date >"+" "+yesterday+" "
							+ "AND date < (SELECT Max(Date) FROM markets WHERE date >"+" "+yesterday+" "
							+ "AND date <"+" "+today+" AND Exchange = "+exchange+")"
							+ "AND Exchange = "+exchange;
		
		try{
			int deleted = DbUtils.deleteRecords(sqlDelete);			
			return deleted;
		}catch(RuntimeException e){
			throw new SpecDbException(e.getMessage());
		}		
	}

}
