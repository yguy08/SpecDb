package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class QueryTable {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static List<Market> genericMarketQuery(String sqlCommand){
		Connection connection = DbConnection.mainConnect();
		List<Market> marketList = genericMarketQuery(connection, sqlCommand);
		try{
			connection.close();
		}catch(SQLException e){
			specLogger.logp(Level.SEVERE, QueryTable.class.getName(), "genericMarketQuery", "Error running query!");
		}
		
		return marketList;
	}
	
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
            		String col_name = rsmd.getColumnName(z);
            		switch(col_name){
            		case "Base":
            			market.setBase(resultSet.getString(z));
            			break;
            		case "Counter":
            			market.setCounter(resultSet.getString(z));
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
            		default:
            			break;
            		}
            	}
            	marketList.add(market);
            }
            tmpStatement.close();
            return marketList;
        } catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
        }
	}

}
