package loader;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

import price.PriceData;

public class FetchNewDb {
	
	private long lastUpdateDate;

	public FetchNewDb(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public void fetchNewRecords(){
		long fetchDate = lastUpdateDate + 24 * 60 * 60;
		System.out.println("Fetch date: " + fetchDate);
		long farFuture = 9999999999L;
		
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		PriceData priceData;
		List<PriceData> priceList = new ArrayList<>();
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
							priceData = new PriceData(currencyPair.toString(),dayData.getDate(),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume());
							priceList.add(priceData);
						}					
				}catch (IOException e) {
					throw new ExchangeException(e.getMessage());
				}
			}
			
			PreparedStatement preparedStatement;
			Connection connection = new Connect().getConnection();
			
	        String compiledQuery = "INSERT INTO markets(Symbol,Date,Open,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?)"; 
	        try{
		        preparedStatement = connection.prepareStatement(compiledQuery);
		        for(PriceData p : priceList){
	        		preparedStatement.setString(1, p.getMarketName());
		        	preparedStatement.setLong(2,p.getDate().getTime()/1000);
		        	preparedStatement.setBigDecimal(3, p.getOpen());
		        	preparedStatement.setBigDecimal(4, p.getHigh());
		        	preparedStatement.setBigDecimal(5,p.getLow());
		        	preparedStatement.setBigDecimal(6, p.getClose());
		        	preparedStatement.setInt(7, p.getVolume().intValue());
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
}
