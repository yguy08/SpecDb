package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.MarketDAO;

public class DbManager {
	
	private Connection connection;
	
	public DbManager(){
		connection = connect();
	}
	
	public List<MarketDAO> genericMarketQuery(String sqlCommand){
        try {
            Statement tmpStatement = connection.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int i = rsmd.getColumnCount();
            System.out.println("Total columns: "+rsmd.getColumnCount());  
            System.out.println("Column Name of 1st column: "+rsmd.getColumnName(1));  
            System.out.println("Column Type Name of 1st column: "+rsmd.getColumnTypeName(1));
            List<MarketDAO> marketList = new ArrayList<>();
            while(resultSet.next()){
            	MarketDAO market = new MarketDAO();
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
	
	public void insertBatchMarkets(List<MarketDAO> marketList, String sqlCommand){
        try {
            PreparedStatement tmpStatement = connection.prepareStatement(sqlCommand);
	        for(int i = 0; i < marketList.size();i++){
        		MarketDAO m = marketList.get(i);
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
    	        System.out.println("adding batch: " + i);
        		long start = System.currentTimeMillis();
        		tmpStatement.executeBatch();
    	        long end = System.currentTimeMillis();
    	        System.out.println("total time taken to insert the batch = " + (end - start) + " ms");
        	}
        }
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
	
	public int deleteRecords(String strSql){
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
	
	public void dropTable(String strSql){
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
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
	
	public void createTable(){
		String strSql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Exchange character NOT NULL,\n"
                + "	Date int NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int,\n"
                + " ATR decimal\n"
                + ");";
        try {
            Statement tmpStatement = connection.createStatement();
            tmpStatement.executeUpdate(strSql);
            System.out.println("Table created!");
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
	
	private Connection connect(){
        String url = "jdbc:sqlite:Speculation1000.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected!");
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
	
	public static void main(String[] args){
		
	}

}
