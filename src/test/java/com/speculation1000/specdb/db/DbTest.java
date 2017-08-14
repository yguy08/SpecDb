package com.speculation1000.specdb.db;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;

public class DbTest {
	
	public static Connection connection;
	
	public static String[] symbolArr = {"BTC","ETH","ETC","XMR","LTC"};
	
	public static BigDecimal price = new BigDecimal(0.01);
	
	public static int volume = 10;
	
	public static List<Market> marketList = new ArrayList<>();
	
	public static Instant startInstant;
	
	public static Instant endInstant;
	
	public static long yesterday;
	
	public static long today;
	
	public static long tomorrow;
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	@BeforeClass
	public static void setUpDb() {
		specLogger.logp(Level.INFO, DbTest.class.getName(), "setUpDb", "Start!");
	    connection = DbConnection.testConnect();
	    DropTable.dropTable(connection);
	    CreateTable.createTable(connection);
	    
	    //insert test data 1 batch of yesterday
	    Market yesterdaysMarket;
	    yesterday = SpecDbDate.getYesterdayEpochSeconds(Instant.now());
	    for(String symbol : symbolArr){
	    	yesterdaysMarket = new Market();
	    	yesterdaysMarket.setBase(symbol);
	    	yesterdaysMarket.setCounter("BTC");
	    	yesterdaysMarket.setExchange("POLO");
	    	yesterdaysMarket.setDate(yesterday);
	    	yesterdaysMarket.setHigh(price);
	    	yesterdaysMarket.setLow(price);
	    	yesterdaysMarket.setOpen(price);
	    	yesterdaysMarket.setClose(price);
	    	yesterdaysMarket.setVolume(volume);
			marketList.add(yesterdaysMarket);	    	
	    }
	    
	    //insert test data from 8/10 0:14 - 8/10 23:59
	    Market todaysMarket;
	    today = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now()) + 14 * 60;
	    for(String symbol : symbolArr){
	    	for(int i = 0;i < 15 * 4 * 24;i+=15){
	    		todaysMarket = new Market();
	    		todaysMarket.setBase(symbol);
	    		todaysMarket.setCounter("BTC");
	    		todaysMarket.setExchange("POLO");
	    		todaysMarket.setDate(today + i*60);
	    		todaysMarket.setHigh(price);
	    		todaysMarket.setLow(price);
	    		todaysMarket.setOpen(price);
	    		todaysMarket.setClose(price);
	    		todaysMarket.setVolume(volume);
				marketList.add(todaysMarket);
	    	}
	    }
	    
	    //insert test data with 8/11 0:14
	    Market tomorrowsMarket;
	    tomorrow = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now()) + 86400 + 14;
	    for(String symbol : symbolArr){
	    	tomorrowsMarket = new Market();
	    	tomorrowsMarket.setBase(symbol);
	    	tomorrowsMarket.setCounter("BTC");
	    	tomorrowsMarket.setExchange("POLO");
	    	tomorrowsMarket.setDate(tomorrow);
	    	tomorrowsMarket.setHigh(price);
	    	tomorrowsMarket.setLow(price);
	    	tomorrowsMarket.setOpen(price);
	    	tomorrowsMarket.setClose(price);
	    	tomorrowsMarket.setVolume(volume);
			marketList.add(tomorrowsMarket);
	    }
	    
	    InsertRecord.insertBatchMarkets(connection, marketList);
	    
	}
	
	@Test
	public void testNextDayCleanUp(){
		try {
			//using tomorrow because dates altered for test purposes to be run at any time
			DbUtils.nextDayCleanUp(connection,"POLO",yesterday,tomorrow);
		} catch (SpecDbException e) {
			e.printStackTrace();
		}
		
		String sqlCommand = "SELECT * FROM Markets where date = " + yesterday;
		List<Market> marketList = QueryTable.genericMarketQuery(connection, sqlCommand);
		for(Market market : marketList){
			assertEquals(yesterday,market.getDate());
		}
	}
	
	
	
    @AfterClass 
    public static void logout() {
	    try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}
