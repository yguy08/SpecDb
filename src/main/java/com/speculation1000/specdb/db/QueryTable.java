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
import com.speculation1000.specdb.trade.SpecDbTrade;

public class QueryTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static List<Market> genericMarketQuery(Connection connection, String sqlCommand){
        try {
            Statement tmpStatement = connection.createStatement();
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
            return marketList;
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}
	
	public static TreeMap<Long,BigDecimal> genericAccountQuery(Connection connection, String sqlCommand){
        try {
        	TreeMap<Long,BigDecimal> accountMap = new TreeMap<>();
            Statement tmpStatement = connection.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            while(resultSet.next()){
            	accountMap.put(resultSet.getLong(1), resultSet.getBigDecimal(2));
            }
            tmpStatement.close();
            return accountMap;
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }		
	}
	
	public static List<SpecDbTrade> genericTradeQuery(Connection connection, String sqlCommand){
        try {
            Statement tmpStatement = connection.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int i = rsmd.getColumnCount();
            List<SpecDbTrade> tradeList = new ArrayList<>();
            while(resultSet.next()){
            	SpecDbTrade sbt = new SpecDbTrade();
            	for(int z = 1; z <= i;z++){
            		String col_name = rsmd.getColumnName(z).toUpperCase();
            		switch(col_name){
            		case "BASE":
            			sbt.setBase(resultSet.getString(z));
            			break;
            		case "COUNTER":
            			sbt.setCounter(resultSet.getString(z));
            			break;	
            		case "EXCHANGE":
            			sbt.setExchange(resultSet.getString(z));
            			break;
            		case "DATE":
            			sbt.setDate(resultSet.getLong(z));
            			break;
            		case "PRICE":
            			sbt.setPrice(resultSet.getBigDecimal(z));
            			break;
            		case "AMOUNT":
            			sbt.setAmount(resultSet.getBigDecimal(z));
            			break;
            		case "TOTAL":
            			sbt.setTotal(resultSet.getBigDecimal(z));
            			break;
            		case "STOP":
            			sbt.setStop(resultSet.getBigDecimal(z));
            			break;
            		case "CURRENTPRICE":
            			sbt.setCurrentPrice(resultSet.getBigDecimal(z));
            			break;
            		case "ISOPEN":
            			sbt.setIsOpen(resultSet.getBoolean(z));
            			break;
            		default:
            			break;
            		}
            	}
            	tradeList.add(sbt);
            }
            tmpStatement.close();
            return tradeList;
        } catch (SQLException ex) {
        	while (ex != null) {
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "genericMarketQuery", ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}

}
