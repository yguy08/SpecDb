package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class InsertRecord {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void insertBatchMarkets(List<Market> marketList){
		Connection connection = DbConnection.mainConnect();
		insertBatchMarkets(connection, marketList);
		try{
			connection.close();
		}catch(SQLException ex){
        	
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
