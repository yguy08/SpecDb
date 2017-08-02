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

public class PoloniexLoader {
	
	public PoloniexLoader(){
		//get last update date
		String lastUpdateSql = "SELECT MAX (Date) AS Date FROM markets WHERE exchange = "+"'POLO'";
		List<MarketDAO> marketList = new DbManager().genericMarketQuery(lastUpdateSql);
		long lastUpdateDate = marketList.get(0).getDate();
		System.out.println("Db populated up to " + lastUpdateDate);
		
		//delete last record before updating all
		String deleteSql = "DELETE from markets WHERE Date = " + lastUpdateDate;
		int recordsDeleted = new DbManager().deleteRecords(deleteSql);
		System.out.println("Records deleted: " + recordsDeleted);
		
		//fetch!
    	long daysMissing = ((Instant.now().getEpochSecond() - lastUpdateDate) / 86400);
		System.out.println("Need next " + daysMissing + " day data");
		fetchNewPoloRecords(lastUpdateDate);
        System.out.println("Db loader complete.");
	}
	
	private void fetchNewPoloRecords(long lastUpdateDate){
		System.out.println("Getting latest data from: " + lastUpdateDate); 
		ZonedDateTime departure = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("Etc/UTC"));
		long farFuture = departure.toInstant().getEpochSecond();
		
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		List<MarketDAO> marketList = new ArrayList<>();
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				List<PoloniexChartData> priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
											.getPoloniexChartData(currencyPair, lastUpdateDate,
											farFuture, PoloniexChartDataPeriodType.PERIOD_86400));
					
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
				
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
			System.out.println("Fetching new " + currencyPair.toString());
		}
			String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
			new DbManager().insertBatchMarkets(marketList, insertQuery);
	}
}
