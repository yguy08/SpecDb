package utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import loader.Connect;
import market.Market;

public class OfflineUtils {
	
	public void batchInsertMarketsOffline() {
	    List<String> marketListFromFile;
	    Market market;
	    List<Market> marketList = new ArrayList<>();		
		
	    try {
			marketListFromFile = Files.readAllLines(Paths.get("marketlist.csv"));
			for(String marketStr : marketListFromFile){
				String marketArr[] = marketStr.split(",");
				String symbol = marketArr[0].concat(marketArr[1]);
				market = new Market(symbol,marketArr[2].trim(),
						Long.parseLong(marketArr[3]),new BigDecimal(marketArr[6].trim()),new BigDecimal(marketArr[4].trim()),
						new BigDecimal(marketArr[5].trim()),new BigDecimal(marketArr[7].trim()),
						Integer.parseInt(marketArr[8].trim()));
				marketList.add(market);
				System.out.println("Added market: " + market.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
        String compiledQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)"; 

        try{
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        for(int i = 0; i < marketList.size();i++){
	        	Market m = marketList.get(i);
	        	preparedStatement.setString(1, m.getSymbol());
	        	preparedStatement.setString(2, m.getExchange());
	        	preparedStatement.setLong(3,m.getDate());
	        	preparedStatement.setBigDecimal(4, m.getHigh());
	        	preparedStatement.setBigDecimal(5, m.getLow());
	        	preparedStatement.setBigDecimal(6,m.getOpen());
	        	preparedStatement.setBigDecimal(7, m.getClose());
	        	preparedStatement.setInt(8, m.getVolume());
	            preparedStatement.addBatch();
	        	if((i % 10000 == 0 && i != 0) || i == marketList.size() - 1){
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
	
	public static void main(String[] args){
		new OfflineUtils().batchInsertMarketsOffline();
		System.out.println("Done!");
	}
}
