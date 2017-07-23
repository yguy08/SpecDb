package loader;

import java.io.IOException;
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

public class UpdateDb {

	long lastUpdateDate;
	
	public UpdateDb(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate / 1000;
	}
	
	public void updateLastRecord(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		PriceData priceData;
		List<PriceData> priceList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw;
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(currencyPair, lastUpdateDate,
								lastUpdateDate, PoloniexChartDataPeriodType.PERIOD_86400));
					
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
	        	preparedStatement.setDate(6,new java.sql.Date(lastUpdateDate*1000));
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

