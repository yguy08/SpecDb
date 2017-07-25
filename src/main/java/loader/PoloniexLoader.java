package loader;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import market.Market;
import utils.SpecDbDates;

public class PoloniexLoader {
	
	public PoloniexLoader(){
		//get last update date
		long lastUpdateDate = lastPoloUpdate();
		System.out.println("Db populated up to " + lastUpdateDate);
	    if(lastUpdateDate > 0){
        	System.out.println("Updating last update date " + lastUpdateDate);
        	updatePoloMarkets(lastUpdateDate);
        	System.out.println("Updated complete...");
        	
        	long daysMissing = (Instant.now().getEpochSecond() - lastUpdateDate) / 60 / 60;
    		System.out.println("Days missing data: " + daysMissing);
	        
	        if(daysMissing > 0){
	        	System.out.println("Need next " + (Instant.now().getEpochSecond() - lastUpdateDate) / 60 / 60 + " day data");
    			fetchNewPoloRecords(lastUpdateDate);
    	        System.out.println("Done...");
	        }else{
	        	System.out.println("Already up to date. Skipping fetch... ");
	        }
        }else{
    		System.out.println("Db full init required.");
			fullPoloRestore();
        }
		System.out.println("Db loader complete.");
	}
	
	private void fetchNewPoloRecords(long lastUpdateDate){
		long fetchDate = lastUpdateDate + 24 * 60 * 60;
		System.out.println("Fetch date: " + fetchDate); 
		ZonedDateTime departure = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("Etc/UTC"));
		long farFuture = departure.toInstant().getEpochSecond();
		
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		Market market;
		List<Market> marketList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw = new ArrayList<>();
		
		try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(currencyPairList.get(currencyPairList.indexOf(CurrencyPair.ETH_BTC)), fetchDate,
								farFuture, PoloniexChartDataPeriodType.PERIOD_86400));
				System.out.println("Checking " + CurrencyPair.ETH_BTC.toString() + " to see if pull is needed");
				System.out.println((CurrencyPair.ETH_BTC.toString()) + priceListRaw.toString().substring(0, 65));
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
		
		if(priceListRaw.get(0).getClose().compareTo(new BigDecimal(0.00)) != 0){
			for(CurrencyPair currencyPair : currencyPairList){
				try {
					priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
							.getPoloniexChartData(currencyPair, fetchDate,
									farFuture, PoloniexChartDataPeriodType.PERIOD_86400));
					
						for(PoloniexChartData dayData : priceListRaw){
							String base = currencyPair.toString().substring(0, currencyPair.toString().indexOf("/"));
							String counter = currencyPair.toString().substring(currencyPair.toString().indexOf("/")+1);
							market = new Market(base,counter,"POLO",dayData.getDate().getTime(),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume().intValue());
							marketList.add(market);
						}					
				}catch (IOException e) {
					throw new ExchangeException(e.getMessage());
				}
			}
			
			PreparedStatement preparedStatement;
			Connection connection = new Connect().getConnection();
			
	        String compiledQuery = "INSERT INTO markets(Base,Counter,Exchange,Date,Open,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?,?,?)"; 
	        try{
		        preparedStatement = connection.prepareStatement(compiledQuery);
				for(Market m : marketList){
		        	preparedStatement.setBigDecimal(1, m.getOpen());
		        	preparedStatement.setBigDecimal(2, m.getHigh());
		        	preparedStatement.setBigDecimal(3,m.getLow());
		        	preparedStatement.setBigDecimal(4, m.getClose());
		        	preparedStatement.setInt(5,m.getVolume());
		        	preparedStatement.setLong(6,lastUpdateDate);
		        	preparedStatement.setString(7,m.getBase());
		        	preparedStatement.setString(8,m.getCounter());
		        	preparedStatement.setString(9,m.getExchange());
		            preparedStatement.addBatch();
	        	}
		        System.out.println("adding new updates...");
        		long start = System.currentTimeMillis();
    	        preparedStatement.executeBatch();
    	        long end = System.currentTimeMillis();
    	        System.out.println("total time taken to insert the batch = " + (end - start) + " ms");
		        
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
		}else{
			System.out.println("System up to date without needing to fetch new days data...");
		}
	}
	
	private long lastPoloUpdate(){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		try{	        
	        String compiledQuery = "SELECT MAX (Date) AS Date FROM markets WHERE exchange = POLO"; 
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        long lastUpdateDate;
	        long start = System.currentTimeMillis();
	        ResultSet resultSet = preparedStatement.executeQuery();
	        long end = System.currentTimeMillis();
	        System.out.println("total time taken to find latest date = " + (end - start) + " ms");
	        
	        resultSet.getLong("Date");
	        if(resultSet.wasNull()){
	        	lastUpdateDate = 0;
	        }else{
	        	lastUpdateDate = resultSet.getLong("Date");
	        }	        
	        preparedStatement.close();
        	connection.close();
        	
        	return lastUpdateDate;
        }catch(SQLException ex){
			System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}
	}
	
	
	private void updatePoloMarkets(long lastUpdateDate){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		Market market;
		List<Market> marketList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw;
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(currencyPair, lastUpdateDate,
								lastUpdateDate, PoloniexChartDataPeriodType.PERIOD_86400));
				for(PoloniexChartData dayData : priceListRaw){
					String base = currencyPair.toString().substring(0, currencyPair.toString().indexOf("/"));
					String counter = currencyPair.toString().substring(currencyPair.toString().indexOf("/")+1);
					market = new Market(base,counter,"POLO",SpecDbDates.dateToUtcMidnightSeconds(dayData.getDate()),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume().intValue());
					marketList.add(market);
				}
				System.out.println(currencyPair.toString());
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
		}
		
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		
		String updateQuery = "UPDATE markets SET Open = ?,"
        		+ "High = ?,"
        		+ "Low = ?,"
        		+ "Close = ?,"
        		+ "Volume = ? "
        		+ "WHERE Date = ? AND base = ? AND counter = ? AND exchange = ? "; 
		
		try{
	        preparedStatement = connection.prepareStatement(updateQuery);
					
			for(Market m : marketList){
	        	preparedStatement.setBigDecimal(1, m.getOpen());
	        	preparedStatement.setBigDecimal(2, m.getHigh());
	        	preparedStatement.setBigDecimal(3,m.getLow());
	        	preparedStatement.setBigDecimal(4, m.getClose());
	        	preparedStatement.setInt(5,m.getVolume());
	        	preparedStatement.setLong(6,lastUpdateDate);
	        	preparedStatement.setString(7,m.getBase());
	        	preparedStatement.setString(8,m.getCounter());
	        	preparedStatement.setString(9,m.getExchange());
	            preparedStatement.addBatch();
        	}
			
			 long start = System.currentTimeMillis();
		     System.out.println("adding updated batch " + marketList.size());
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
	
	private void fullPoloRestore(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		Market market;
		List<Market> marketList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw;
		long date = new Date().getTime() / 1000;
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
							.getPoloniexChartData(currencyPair, date - 365 * 10 * 24 * 60 * 60,
								date, PoloniexChartDataPeriodType.PERIOD_86400));
					for(PoloniexChartData dayData : priceListRaw){
						String base = currencyPair.toString().substring(0, currencyPair.toString().indexOf("/"));
						String counter = currencyPair.toString().substring(currencyPair.toString().indexOf("/")+1);
						market = new Market(base,counter,"POLO",SpecDbDates.dateToUtcMidnightSeconds(dayData.getDate()),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume().intValue());
						marketList.add(market);
					}
					System.out.println("Loaded " + currencyPair.toString());
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
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
	        	preparedStatement.setLong(4, m.getDate());
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

}
