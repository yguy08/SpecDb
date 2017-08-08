package com.speculation1000.specdb.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.utils.SpecDbDate;

public class DbTest {
	
	public static Connection connection;
	
	public static String[] symbolArr = {"BTCUSDT","ETHBTC","ETCBTC","XMRBTC","LTCBTC","STEEMBTC","STRATBTC","DGBBTC"};
	
	public static BigDecimal price = new BigDecimal(0.01);
	
	public static int volume = 10;
	
	public static List<Market> marketList = new ArrayList<>();
	
	public static Instant startInstant;
	
	public static Instant endInstant;
	
	@BeforeClass
	public static void setUpDb() {
	    connection = DbUtils.testConnect();
	    DbUtils.dropTable(connection);
	    DbUtils.createTable(connection);
	    startInstant = SpecDbDate.getTodayMidnightInstant(Instant.now());
	    Market market;
	    for(String symbol : symbolArr){
	    	market = new Market();
			market.setSymbol(symbol);
			market.setExchange("POLO");
			market.setDate(startInstant.getEpochSecond());
			market.setHigh(price);
			market.setLow(price);
			market.setOpen(price);
			market.setClose(price);
			market.setVolume(volume);
			marketList.add(market);
		    	
			for(int i = 1;i < 98;i++){
				endInstant = startInstant.plusSeconds(i*15*60 - (60));
	    		market = new Market();
				market.setSymbol(symbol);
				market.setExchange("POLO");
				market.setDate(endInstant.getEpochSecond());
				market.setHigh(price);
				market.setLow(price);
				market.setOpen(price);
				market.setClose(price);
				market.setVolume(volume);
				marketList.add(market);	    		
		    }
	    }
	    
	    DbUtils.insertBatchMarkets(connection, marketList);
	    
	    try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNextDayCleanUp(){
			
	}
	
	
	
    @AfterClass 
    public static void logout() {
    	
    }

}
