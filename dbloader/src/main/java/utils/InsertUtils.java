package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import loader.Connect;
import price.PriceData;

public class InsertUtils {
	
	public void insertNewRecords(List<PriceData> priceList){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
        String compiledQuery = "INSERT INTO markets(Symbol,Date,Open,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?)"; 

        try{
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        for(int i = 0; i < priceList.size();i++){
	        	PriceData p = priceList.get(i);
	        	preparedStatement.setString(1, p.getMarketName());
	        	preparedStatement.setLong(2, p.getDate().getTime());
	        	preparedStatement.setBigDecimal(3, p.getOpen());
	        	preparedStatement.setBigDecimal(4, p.getHigh());
	        	preparedStatement.setBigDecimal(5,p.getLow());
	        	preparedStatement.setBigDecimal(6, p.getClose());
	        	preparedStatement.setInt(7, p.getVolume().intValue());
	            preparedStatement.addBatch();
	        	if((i % 10000 == 0 && i != 0) || i == priceList.size() - 1){
	    	        System.out.println("adding batch: " + (i-10000) + "-" + i);
	        		long start = System.currentTimeMillis();
	    	        preparedStatement.executeBatch();
	    	        long end = System.currentTimeMillis();
	    	        System.out.println("total time taken to insert the batch = " + (end - start) + " ms");
	        	}
	        }
	        preparedStatement.close();
	        connection.close();
		}catch (SQLException ex) {
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
	    }
	}

}
