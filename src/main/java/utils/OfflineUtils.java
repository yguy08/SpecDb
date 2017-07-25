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
			marketListFromFile = Files.readAllLines(Paths.get("src/main/resources/offline/markets.csv"));
			for(String marketStr : marketListFromFile){
				String marketArr[] = marketStr.split(",");
				String baseStr = marketArr[0].substring(0, marketArr[0].indexOf(":"));
				String base;
				String counter;
				if(baseStr.endsWith("BTC")){
					base = baseStr.substring(0, baseStr.lastIndexOf("BTC"));
					counter = baseStr.substring(baseStr.lastIndexOf("BTC"));
				}else if(baseStr.endsWith("ETH")){
					base = baseStr.substring(0, baseStr.lastIndexOf("ETH"));
					counter = baseStr.substring(baseStr.lastIndexOf("ETH"));
				}else if(baseStr.endsWith("USDT")){
					base = baseStr.substring(0, baseStr.lastIndexOf("USDT"));
					counter = baseStr.substring(baseStr.lastIndexOf("USDT"));
				}else{
					base = baseStr.substring(0, baseStr.lastIndexOf("XMR"));
					counter = baseStr.substring(baseStr.lastIndexOf("XMR"));
				}
				
				market = new Market(base,counter,"POLO",SpecDbDates.simpleDateToUtcMidnightSeconds(marketArr[1].trim()),
						new BigDecimal(marketArr[2].trim()),new BigDecimal(marketArr[3].trim()),
						new BigDecimal(marketArr[4].trim()),new BigDecimal(marketArr[5].trim()),
						Integer.parseInt(marketArr[6].trim()));
				marketList.add(market);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
        String compiledQuery = "INSERT INTO markets(Base,Counter,Exchange,Date,Open,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?,?,?)"; 

        try{
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        for(int i = 0; i < marketList.size();i++){
	        	Market m = marketList.get(i);
	        	preparedStatement.setString(1, m.getBase());
	        	preparedStatement.setString(2, m.getCounter());
	        	preparedStatement.setString(3, m.getExchange());
	        	preparedStatement.setLong(4,m.getDate());
	        	preparedStatement.setBigDecimal(5, m.getOpen());
	        	preparedStatement.setBigDecimal(6, m.getHigh());
	        	preparedStatement.setBigDecimal(7,m.getLow());
	        	preparedStatement.setBigDecimal(8, m.getClose());
	        	preparedStatement.setInt(9, m.getVolume());
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
