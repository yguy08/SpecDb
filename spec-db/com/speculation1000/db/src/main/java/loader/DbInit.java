package loader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

import price.PriceData;

public class DbInit {

	public DbInit() {
				
	}
	
	public void batchInsertMarkets(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		PriceData priceData;
		List<PriceData> priceList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw;
		long date = new Date().getTime() / 1000;
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
							.getPoloniexChartData(currencyPair, date - 365 * 10 * 24 * 60 * 60,
								date, PoloniexChartDataPeriodType.PERIOD_86400));
					for(PoloniexChartData dayData : priceListRaw){
						priceData = new PriceData(currencyPair.toString(),dayData.getDate(),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume());
						priceList.add(priceData);
					}
					System.out.println(currencyPair.toString());
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
		}
		
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
        String compiledQuery = "INSERT INTO markets(Symbol,Date,Open,High,Low,Close,Volume) VALUES(?,?,?,?,?,?,?)"; 

        try{
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        for(int i = 0; i < priceList.size();i++){
	        	PriceData p = priceList.get(i);
	        	preparedStatement.setString(1, p.getMarketName());
	        	preparedStatement.setLong(2,p.getDate().getTime() / 1000);
	        	preparedStatement.setBigDecimal(3, p.getOpen());
	        	preparedStatement.setBigDecimal(4, p.getHigh());
	        	preparedStatement.setBigDecimal(5,p.getLow());
	        	preparedStatement.setBigDecimal(6, p.getClose());
	        	preparedStatement.setInt(7, p.getVolume().intValue());
	            preparedStatement.addBatch();
	        	if((i % 10000 == 0 && i != 0) || i == priceList.size() - 1){
	    	        System.out.println("adding batch + " + i /10000 + "...");
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
