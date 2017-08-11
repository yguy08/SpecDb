package com.speculation1000.specdb.dao;

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

import com.speculation1000.specdb.db.CreateTable;
import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DropTable;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.time.SpecDbDate;

public class MarketSummaryDAOTest {
	
	public static Connection connection;
	
	public static String[] symbolArr = {"BTCUSDT","ETHBTC","ETCBTC","XMRBTC","LTCBTC"};
	
	public static int price = 0;
	
	public static int volume = 10;
	
	public static List<Market> marketList = new ArrayList<>();
	
	public static long start = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now().minusSeconds(86400*10));
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	@BeforeClass
	public static void setUpDb() {
		specLogger.logp(Level.INFO, MarketSummaryDAOTest.class.getName(), "setUpDb", "Start!");
	    connection = DbConnection.testConnect();
	    DropTable.dropTable(connection);
	    CreateTable.createTable(connection);
	    
	    //insert 10 days of data
	    Market market;	    
	    for(String symbol : symbolArr){
	    	for(ExchangeEnum exchange : ExchangeEnum.values()){
	    		price = 0;
		    	for(int i = 0; i < 10; i++){
		    		market = new Market();
		    		market.setSymbol(symbol);
		    		market.setExchange(exchange.getExchangeSymbol());
		    		market.setDate(start + (i*86400));
		    		market.setHigh(new BigDecimal(price++));
		    		market.setLow(new BigDecimal(price++));
		    		market.setOpen(new BigDecimal(price++));
		    		market.setClose(new BigDecimal(price++));
		    		market.setVolume(volume);
					marketList.add(market);
				}
	    	}
	    }
	    InsertRecord.insertBatchMarkets(connection, marketList);
	}
	
	@Test
	public void getOldestRecordByExchange(){
		for(ExchangeEnum exchange : ExchangeEnum.values()){
			long date = MarketSummaryDAO.getOldestRecordByExchange(connection, exchange.getExchangeSymbol());
			assertEquals(start,date);
		}
	}
	
	@Test
	public void getEntries(){
		
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
