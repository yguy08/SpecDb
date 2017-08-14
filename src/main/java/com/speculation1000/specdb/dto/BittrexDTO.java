package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;

public class BittrexDTO implements ExchangeDTO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	@Override
	public List<Market> getLatestMarketList() throws SpecDbException {
		Map<CurrencyPair,Ticker> bittrexChartData = 
				getBittrexChartData();
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<CurrencyPair, Ticker> e : bittrexChartData.entrySet()){
				Market market = new Market();
				Ticker t = e.getValue();
				market.setBase(e.getKey().base.toString());
				market.setCounter(e.getKey().counter.toString());
				market.setExchange(ExchangeEnum.BITTREX.getExchangeSymbol());
				market.setDate(StartRun.getStartRunTS().getEpochSecond());
				market.setHigh(t.getHigh());
				market.setLow(t.getLow());
				market.setClose(t.getLast());
				market.setVolume(t.getVolume().intValue());
				marketList.add(market);
			}
		return marketList;
	}

	@Override
	public List<Market> fetchExchangeHistory(long endDate) {
		//TODO - https://bittrex.com/Api/v2.0/pub/market/GetTicks?marketName=BTC-WAVES&tickInterval=day&_=1499127220008
		return null;
	}
	
	private Map<CurrencyPair,Ticker> getBittrexChartData(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
		Map<CurrencyPair,Ticker> trexTickerMap = new HashMap<>();
		List<CurrencyPair> pairList = exchange.getExchangeSymbols();
		specLogger.log(BittrexDTO.class.getName(),  "Loading TREX markets from Trex API...");
		for(CurrencyPair pair : pairList){
			try {
				Ticker trexTicker = exchange.getMarketDataService().getTicker(pair);
						trexTickerMap.put(pair, trexTicker);
			} catch (IOException e) {
				StringBuffer sb = new StringBuffer();
				for(StackTraceElement ste : e.getStackTrace()){
					sb.append(" [" + ste.toString() + "],");
				}
				specLogger.log(PoloniexDTO.class.getName(), sb.toString());
			}
		}
		return trexTickerMap;
	}

}
