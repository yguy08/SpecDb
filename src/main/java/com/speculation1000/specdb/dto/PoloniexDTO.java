package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.time.SpecDbDate;

public class PoloniexDTO implements ExchangeDTO {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant OLD_DATE = Instant.ofEpochSecond(0);

	@Override
	public List<Market> getLatestMarketList() {
		Instant start = StartRun.getStartRunTS();
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
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		Map<CurrencyPair,List<PoloniexChartData>> poloChartDataMap = new HashMap<>();
		List<CurrencyPair> pairList = exchange.getExchangeSymbols();
		specLogger.logp(Level.INFO, PoloniexDTO.class.getName(), "getPoloniexChartData", "Loading Polo markets from Polo API...");
		for(CurrencyPair pair : pairList){
			try {
				List<PoloniexChartData> poloniexChartData = (Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(pair, start,
						end, PoloniexChartDataPeriodType.PERIOD_86400)));
				poloChartDataMap.put(pair, poloniexChartData);
			} catch (IOException e) {
				specLogger.logp(Level.SEVERE, PoloniexDTO.class.getName(), "getPoloniexChartData", "Failed to load Polo markets from POLO API...");
			}
		}
		return poloChartDataMap;
	}

}
