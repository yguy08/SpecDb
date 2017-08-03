package loader;

import java.io.IOException;
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

import dao.MarketDAO;
import db.DbManager;
import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class PoloniexLoader {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void poloUpdater(){
		Exchange exchange = null;
		try{
			exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		}catch(Exception cause){
		    System.out.println(cause);
		}
		
		if(exchange != null){
			List<CurrencyPair> pairs = exchange.getExchangeSymbols();
			List<MarketDAO> marketList = new ArrayList<>();
			List<PoloniexChartData> chartData = new ArrayList<>();
			for(CurrencyPair pair : pairs){
				
				try{
					chartData = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
							.getPoloniexChartData(pair, Instant.now().getEpochSecond(),
							9999999999L, PoloniexChartDataPeriodType.PERIOD_86400));
				}catch(Exception e){
					System.out.println(e);
				}
				
				for(PoloniexChartData dayData : chartData){
					MarketDAO market = new MarketDAO();
					market.setSymbol(pair.base.toString() + pair.counter.toString());
					market.setExchange("POLO");
					market.setDate(SpecDbDate.dateToUtcMidnightSeconds(dayData.getDate()));
					market.setHigh(dayData.getHigh());
					market.setLow(dayData.getLow());
					market.setOpen(dayData.getOpen());
					market.setClose(dayData.getClose());
					market.setVolume(dayData.getVolume().intValue());
					marketList.add(market);
				}			
			}
			
			if(SpecDbDate.isNewDay()){
				String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
				new DbManager().insertBatchMarkets(marketList, insertQuery);
			}else{
	    		String deleteSql = "DELETE from markets WHERE Date = (SELECT Max(Date) from markets where Exchange = 'POLO') AND Exchange = 'POLO'";
	    		int recordsDeleted = new DbManager().deleteRecords(deleteSql);
	    		specLogger.log(DbLoader.class.getName(), "Same Day..Records deleted: " + recordsDeleted);
				String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
				new DbManager().insertBatchMarkets(marketList, insertQuery);
			}			
		}else{
			specLogger.log(DbLoader.class.getName(), "Something went wrong!");
		}
		

	}
	
	public PoloniexLoader(){
		String lastUpdateSql = "SELECT MAX (Date) AS Date FROM markets WHERE exchange = "+"'POLO'";
		List<MarketDAO> marketList = new DbManager().genericMarketQuery(lastUpdateSql);
		long lastUpdateDate = marketList.get(0).getDate();
		System.out.println("Db populated up to " + lastUpdateDate);
		specLogger.log(DbLoader.class.getName(), "Last POLO update: " + lastUpdateDate);
		
    	long daysMissing = ((Instant.now().getEpochSecond() - lastUpdateDate) / 86400);
    	if(daysMissing <= 1){
    		//delete last record before updating all
    		String deleteSql = "DELETE from markets WHERE Date = " + lastUpdateDate;
    		int recordsDeleted = new DbManager().deleteRecords(deleteSql);
    		specLogger.log(DbLoader.class.getName(), "Records deleted: " + recordsDeleted);
    	}
		
		//fetch!
		specLogger.log(DbLoader.class.getName(), "Need next " + daysMissing + " day data");
		fetchNewPoloRecords(lastUpdateDate);
		specLogger.log(DbLoader.class.getName(), "Done loading Polo markets");
	}
	
	private void fetchNewPoloRecords(long lastUpdateDate){
		ZonedDateTime departure = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("Etc/UTC"));
		long farFuture = departure.toInstant().getEpochSecond();
		Exchange exchange = null;
		
		try{
			exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		}catch(Exception cause){
		    System.out.println(cause);
		}
		
		List<CurrencyPair> currencyPairList = new ArrayList<>();
		List<MarketDAO> marketList = new ArrayList<>();
		if(exchange != null){
			currencyPairList = exchange.getExchangeSymbols();
		}
		
		for(CurrencyPair currencyPair : currencyPairList){
			List<PoloniexChartData> priceListRaw = new ArrayList<>();
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
											.getPoloniexChartData(currencyPair, lastUpdateDate,
											farFuture, PoloniexChartDataPeriodType.PERIOD_86400));
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
				
			for(PoloniexChartData dayData : priceListRaw){
					MarketDAO market = new MarketDAO();
					market.setSymbol(currencyPair.base.toString() + currencyPair.counter.toString());
					market.setExchange("POLO");
					market.setDate(SpecDbDate.dateToUtcMidnightSeconds(dayData.getDate()));
					market.setHigh(dayData.getHigh());
					market.setLow(dayData.getLow());
					market.setOpen(dayData.getOpen());
					market.setClose(dayData.getClose());
					market.setVolume(dayData.getVolume().intValue());
					marketList.add(market);
			}
			specLogger.log(DbLoader.class.getName(), "Done loading Polo markets");
		}
		
			String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
			new DbManager().insertBatchMarkets(marketList, insertQuery);
			specLogger.log(DbLoader.class.getName(), "Done inserting POLO markets into DB");
	}
}
