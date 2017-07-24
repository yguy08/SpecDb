package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import loader.Connect;
import price.PriceData;

public class UpdateUtils {
	
	public void updateLatestRecord(List<PriceData> priceList, long lastUpdateDate){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		
		String updateQuery = "UPDATE markets SET Open = ?,"
        		+ "High = ?,"
        		+ "Low = ?,"
        		+ "Close = ?,"
        		+ "Volume = ? "
        		+ "WHERE Date = ? AND Symbol = ?"; 
		
		try{
	        preparedStatement = connection.prepareStatement(updateQuery);
					
			for(PriceData p : priceList){
	        	preparedStatement.setBigDecimal(1, p.getOpen());
	        	preparedStatement.setBigDecimal(2, p.getHigh());
	        	preparedStatement.setBigDecimal(3,p.getLow());
	        	preparedStatement.setBigDecimal(4, p.getClose());
	        	preparedStatement.setInt(5,p.getVolume().intValue());
	        	preparedStatement.setLong(6,lastUpdateDate);
	        	preparedStatement.setString(7,p.getMarketName().toString());
	            preparedStatement.addBatch();
        	}
			
			 long start = System.currentTimeMillis();
		     System.out.println("adding updated batch " + priceList.size());
		     preparedStatement.executeBatch();
		     long end = System.currentTimeMillis();
		     System.out.println("total time taken to insert the batch = " + (end - start) + " ms");
		     preparedStatement.close();
		     connection.close();
		}catch(SQLException ex){
			System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}
	}

}
