package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.time.SpecDbDate;

public class PoloniexDTO implements ExchangeDTO {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant OLD_DATE = Instant.ofEpochSecond(0);

	@Override
	public List<Market> getLatestMarketList() {
		
		Map<String,List<PoloniexChartData>> poloniexChartData = 
				getPoloniexChartData(SpecDbDate.getTodayUtcEpochSeconds(StartRun.getStartRunTS()), 9999999999L);
		
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<String, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
			for(PoloniexChartData dayData : e.getValue()){
				Market market = new Market();
				market.setSymbol(e.getKey());
				market.setExchange("POLO");
				market.setDate(StartRun.getStartRunTS().getEpochSecond());
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
	public List<Market> fetchEntireExchangeHistory() {
		
		Map<String,List<PoloniexChartData>> poloniexChartData = 
				getPoloniexChartData(OLD_DATE.getEpochSecond(), 9999999999L);
		
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<String, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
			for(PoloniexChartData dayData : e.getValue()){
				Market market = new Market();
				market.setSymbol(e.getKey());
				market.setExchange("POLO");
				market.setDate(SpecDbDate.getTodayUtcEpochSeconds(Instant.ofEpochMilli(dayData.getDate().getTime())));
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
	
	private Map<String,List<PoloniexChartData>> getPoloniexChartData(long start, long end){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		Map<String,List<PoloniexChartData>> poloChartDataMap = new HashMap<>();
		List<CurrencyPair> pairList = exchange.getExchangeSymbols();
		specLogger.log(PoloniexDTO.class.getName(),  "Loading Polo markets from Polo API...");
		for(CurrencyPair pair : pairList){
			try {
				List<PoloniexChartData> poloniexChartData = (Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(pair, start,
						end, PoloniexChartDataPeriodType.PERIOD_86400)));
				poloChartDataMap.put(pair.base.toString() + pair.counter.toString(), poloniexChartData);
			} catch (IOException e) {
				StringBuffer sb = new StringBuffer();
				for(StackTraceElement ste : e.getStackTrace()){
					sb.append(" [" + ste.toString() + "],");
				}
				specLogger.log(PoloniexDTO.class.getName(), sb.toString());
			}
		}
		return poloChartDataMap;
	}

}
