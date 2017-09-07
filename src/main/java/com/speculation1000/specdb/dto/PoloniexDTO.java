package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexAccountServiceRaw;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;
import org.knowm.xchange.poloniex.service.PoloniexTradeService;
import org.knowm.xchange.service.trade.TradeService;

import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.Config;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.time.SpecDbDate;

public class PoloniexDTO implements ExchangeDTO {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant OLD_DATE = Instant.ofEpochSecond(0);
	
	private static Exchange poloDefault; 
	
	private static Exchange poloAuthenticated; 
	
	static{
		try{
			poloDefault	= ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
			poloAuthenticated	= initAuthenticatedExchange();
		}catch(Exception e){
			try {
				throw new Exception(e.getMessage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public List<Market> getLatestMarketList() {
		Instant start = StandardMode.getStartRunTS();
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(start);
		long end = 9999999999L;		
		
		Map<CurrencyPair,List<PoloniexChartData>> poloniexChartData = 
				getPoloniexChartData(todayMidnight, end);
		
		specLogger.logp(Level.INFO, PoloniexDTO.class.getName(), "getLatestMarketList", "Fetched latest Polo markets for: " 
				+ SpecDbDate.longToLogStringFormat(todayMidnight));
		
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<CurrencyPair, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
			for(PoloniexChartData dayData : e.getValue()){
				Market market = new Market();
				market.setBase(e.getKey().base.toString());
				market.setCounter(e.getKey().counter.toString());
				market.setExchange(ExchangeEnum.POLONIEX.getExchangeSymbol());
				market.setDate(todayMidnight);
				market.setHigh(dayData.getHigh());
				market.setLow(dayData.getLow());
				market.setOpen(dayData.getOpen());
				market.setClose(dayData.getClose());
				market.setVolume(dayData.getVolume().intValue());
				marketList.add(market);
			}
		}
		return marketList;
	}

	@Override
	public List<Market> fetchExchangeHistory(long endDate) {
		
		Map<CurrencyPair,List<PoloniexChartData>> poloniexChartData = 
				getPoloniexChartData(OLD_DATE.getEpochSecond(), endDate);
		specLogger.logp(Level.INFO, PoloniexDTO.class.getName(), "fetchExchangeHistory", "Fetched Polo history from " 
				+ SpecDbDate.instantToLogStringFormat(OLD_DATE) + " " + "to " + SpecDbDate.instantToLogStringFormat(Instant.ofEpochSecond(endDate)));
		
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<CurrencyPair, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
			for(PoloniexChartData dayData : e.getValue()){
				Market market = new Market();
				market.setBase(e.getKey().base.toString());
				market.setCounter(e.getKey().counter.toString());
				market.setExchange(ExchangeEnum.POLONIEX.getExchangeSymbol());
				market.setDate(SpecDbDate.getTodayMidnightEpochSeconds(Instant.ofEpochMilli(dayData.getDate().getTime())));
				market.setHigh(dayData.getHigh());
				market.setLow(dayData.getLow());
				market.setOpen(dayData.getOpen());
				market.setClose(dayData.getClose());
				market.setVolume(dayData.getVolume().intValue());
				marketList.add(market);
			}
		}
		return marketList;
	}
	
	private Map<CurrencyPair,List<PoloniexChartData>> getPoloniexChartData(long start, long end){
		Map<CurrencyPair,List<PoloniexChartData>> poloChartDataMap = new HashMap<>();
		List<CurrencyPair> pairList = poloDefault.getExchangeSymbols();
		specLogger.logp(Level.INFO, PoloniexDTO.class.getName(), "getPoloniexChartData", "Loading Polo markets from Polo API...");
		for(CurrencyPair pair : pairList){
			try {
				List<PoloniexChartData> poloniexChartData = (Arrays.asList(((PoloniexMarketDataServiceRaw) poloDefault.getMarketDataService())
						.getPoloniexChartData(pair, start,
						end, PoloniexChartDataPeriodType.PERIOD_86400)));
				poloChartDataMap.put(pair, poloniexChartData);
			} catch (IOException e) {
				specLogger.logp(Level.SEVERE, PoloniexDTO.class.getName(), "getPoloniexChartData", "Failed to load Polo markets from POLO API...");
			}
		}
		return poloChartDataMap;
	}

	@Override
	public BigDecimal getAccountBalance(TreeMap<String,BigDecimal> currentCloseMap) throws SpecDbException {
		if(poloAuthenticated == null){
			throw new SpecDbException("Polo exchange is not initialized");
		}
		BigDecimal balance = new BigDecimal(0.00);
		PoloniexAccountServiceRaw accountService = (PoloniexAccountServiceRaw) poloAuthenticated.getAccountService();
		try {
			for(Balance b : accountService.getWallets()) {
				if(b.getTotal().compareTo(new BigDecimal(0.00)) > 0) {
					if(!b.getCurrency().equals(Currency.BTC)) {
						BigDecimal currentPrice = currentCloseMap.get(b.getCurrency()+"BTC"+":"+"POLO");
						BigDecimal btcTotal = currentPrice.multiply(b.getTotal());
						balance = balance.add(btcTotal);
					}else {
						balance = balance.add(b.getTotal());
					}
				}
			}
		} catch (IOException e) {
			throw new SpecDbException(e.getMessage());
		}
		return balance;
	}
	
	private static Exchange initAuthenticatedExchange() {
		  ExchangeSpecification spec = new ExchangeSpecification(PoloniexExchange.class);
	      Config config = new Config();
		  spec.setApiKey(config.getPoloKey());
		  spec.setSecretKey(config.getPoloSecret());
		  return ExchangeFactory.INSTANCE.createExchange(spec);
	}

	public void getOpenTrades() {
		TradeService tradeService = poloAuthenticated.getTradeService();
		PoloniexAccountServiceRaw accountService = (PoloniexAccountServiceRaw) poloAuthenticated.getAccountService();
		BigDecimal balance = new BigDecimal(0.00);
		Map<CurrencyPair,List<Trade>> tradeMap = new HashMap<>();
		try {
			for(Balance b : accountService.getWallets()) {
				if(b.getTotal().compareTo(new BigDecimal(0.00)) > 0) {
					if(!b.getCurrency().equals(Currency.BTC)) {
						CurrencyPair currencyPair = new CurrencyPair(b.getCurrency(),Currency.BTC);
					    PoloniexTradeService.PoloniexTradeHistoryParams params = new PoloniexTradeService.PoloniexTradeHistoryParams();
					    params.setCurrencyPair(currencyPair);
					    params.setStartTime(Date.from(Instant.now().minusSeconds(86400 * 10 * 365)));
					    params.setEndTime(new Date());
					    List<Trade> tradeList = new ArrayList<>();
					    for(Trade ut : tradeService.getTradeHistory(params).getTrades()) {
					    	tradeList.add(ut);					    	
					    }
					    tradeMap.put(currencyPair, tradeList);
					}else {
						balance = balance.add(b.getTotal());
					}
				}
			}
			for(Map.Entry<CurrencyPair, List<Trade>> e : tradeMap.entrySet()){
				if(e.getValue().size() > 0) {
					Instant first = Instant.ofEpochMilli(e.getValue().get(e.getValue().size()-1).getTimestamp().getTime());
					if(first.compareTo(Instant.now().minusSeconds(86400 * 31)) >= 0) {

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
