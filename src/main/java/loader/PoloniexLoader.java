package loader;

import java.io.IOException;
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
		
		//delete last record before updating all
		deleteRecords(lastUpdateDate);
		
		//fetch!
    	long daysMissing = ((Instant.now().getEpochSecond() - lastUpdateDate) / 86400);
		System.out.println("Need next " + daysMissing + " day data");
		fetchNewPoloRecords(lastUpdateDate);
        System.out.println("Db loader complete.");
	}
	
	private void deleteRecords(long lastUpdateDate){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		
		String deleteQuery = "DELETE from markets WHERE Date = " + lastUpdateDate; 
		
		try{
			preparedStatement = connection.prepareStatement(deleteQuery);
		
			long start = System.currentTimeMillis();
			System.out.println("deleting last record before updating...");
			int i = preparedStatement.executeUpdate();
			long end = System.currentTimeMillis();
			System.out.println("total time taken to delete = " + (end - start) + " ms");
			System.out.println("total records deleted: " + i);
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
	
	private void fetchNewPoloRecords(long lastUpdateDate){
		System.out.println("Getting latest data from: " + lastUpdateDate); 
		ZonedDateTime departure = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("Etc/UTC"));
		long farFuture = departure.toInstant().getEpochSecond();
		
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		Market market;
		List<Market> marketList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw = new ArrayList<>();
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
							.getPoloniexChartData(currencyPair, lastUpdateDate,
									farFuture, PoloniexChartDataPeriodType.PERIOD_86400));
					
						for(PoloniexChartData dayData : priceListRaw){
							String symbol = currencyPair.base.toString() + currencyPair.counter.toString();
							market = new Market(symbol,"POLO",SpecDbDates.dateToUtcMidnightSeconds(dayData.getDate()),
									dayData.getHigh(),dayData.getLow(),dayData.getOpen(),
									dayData.getClose(),dayData.getVolume().intValue());
							marketList.add(market);
						}					
				}catch (IOException e) {
					throw new ExchangeException(e.getMessage());
				}
			}
			
			PreparedStatement preparedStatement;
			Connection connection = new Connect().getConnection();
			
	        String compiledQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)"; 
	        try{
		        preparedStatement = connection.prepareStatement(compiledQuery);
				for(Market m : marketList){
		        	preparedStatement.setString(1, m.getSymbol());
		        	preparedStatement.setString(2, m.getExchange());
		        	preparedStatement.setLong(3,m.getDate());
		        	preparedStatement.setBigDecimal(4, m.getHigh());
		        	preparedStatement.setBigDecimal(5, m.getLow());
		        	preparedStatement.setBigDecimal(6,m.getOpen());
		        	preparedStatement.setBigDecimal(7, m.getClose());
		        	preparedStatement.setInt(8,m.getVolume());
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
		
	}
	
	private long lastPoloUpdate(){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		try{	        
	        String compiledQuery = "SELECT MAX (Date) AS Date FROM markets WHERE exchange = "+"'POLO'"; 
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

}
