package com.speculation1000.specdb.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.MarketEntry;

public class QueryTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static List<Market> genericMarketQuery(DbConnectionEnum dbce, String sqlCommand){
        try {
        	Connection conn = DbConnection.connect(dbce);
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
            		case "BASE":
            			market.setBase(resultSet.getString(z));
            			break;
            		case "COUNTER":
            			market.setCounter(resultSet.getString(z));
            			break;	
            		case "EXCHANGE":
            			market.setExchange(resultSet.getString(z));
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
            		case "OPEN":
            			market.setOpen(resultSet.getBigDecimal(z));
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
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static TreeMap<Long,BigDecimal> genericAccountQuery(DbConnectionEnum dbce, String sqlCommand){
        try {
        	Connection conn = DbConnection.connect(dbce);
        	TreeMap<Long,BigDecimal> accountMap = new TreeMap<>();
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            while(resultSet.next()){
            	accountMap.put(resultSet.getLong(1), resultSet.getBigDecimal(2));
            }
            tmpStatement.close();
            conn.close();
            return accountMap;
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }		
	}
	
	public static void genericTradeQuery(){
 
	}

	public static List<MarketEntry> genericEntryQuery(DbConnectionEnum dbce, String sqlCommand) {
        try {
        	Connection conn = DbConnection.connect(dbce);
            Statement tmpStatement = conn.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int i = rsmd.getColumnCount();
            List<MarketEntry> marketEntryList = new ArrayList<>();
            while(resultSet.next()){
            	MarketEntry marketentry = new MarketEntry();
            	for(int z = 1; z <= i;z++){
            		String col_name = rsmd.getColumnName(z).toUpperCase();
            		switch(col_name){
            		case "SYMBOL":
            			marketentry.setSymbol(resultSet.getString(z));
            			break;
            		case "DATE":
            			marketentry.setDate(resultSet.getLong(z));
            			break;	
            		case "PRICE":
            			marketentry.setPrice(resultSet.getBigDecimal(z));
            			break;
            		case "ATR":
            			marketentry.setAtr(resultSet.getBigDecimal(z));
            			break;
            		case "AMOUNT":
            			marketentry.setAmount(resultSet.getBigDecimal(z));
            			break;
            		case "TOTAL":
            			marketentry.setTotal(resultSet.getBigDecimal(z));
            			break;
            		case "DAYHIGHLOW":
            			marketentry.setDayHighLow(resultSet.getInt(z));
            			break;
            		case "DIRECTION":
            			marketentry.setDirection(resultSet.getString(z));
            			break;
            		case "STOP":
            			marketentry.setStop(resultSet.getBigDecimal(z));
            			break;
            		default:
            			break;
            		}
            	}
            	marketEntryList.add(marketentry);
            }
            tmpStatement.close();
            conn.close();
            return marketEntryList;
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }		
	}

}
