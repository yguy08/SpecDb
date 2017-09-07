package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexTicker;
import org.knowm.xchange.bittrex.v1.service.BittrexMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.time.SpecDbDate;

public class BittrexDTO implements ExchangeDTO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	@Override
	public List<Market> getLatestMarketList() throws SpecDbException {
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(StandardMode.getStartRunTS());
		Map<CurrencyPair, BittrexTicker> bittrexChartData;
		try {
			bittrexChartData = getBittrexChartData();
			specLogger.logp(Level.INFO, BittrexDTO.class.getName(), "getLatestMarketList", "Got Bittrex Chart Data");
		} catch (IOException e1) {
			throw new SpecDbException(e1.getMessage());
		}
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<CurrencyPair, BittrexTicker> e : bittrexChartData.entrySet()){
				Market market = new Market();
				BittrexTicker t = e.getValue();
				market.setBase(e.getKey().base.toString());
				market.setCounter(e.getKey().counter.toString());
				market.setExchange(ExchangeEnum.BITTREX.getExchangeSymbol());
				market.setDate(todayMidnight);
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
	
	private Map<CurrencyPair, BittrexTicker> getBittrexChartData() throws IOException{
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
		BittrexMarketDataService bmds = (BittrexMarketDataService) exchange.getMarketDataService();
		List<BittrexTicker> tickerList = bmds.getBittrexTickers();
		Map<CurrencyPair,BittrexTicker> trexTickerMap = new HashMap<>();
		for(BittrexTicker t : tickerList){
			//trex is opposite
			String base = t.getMarketName().substring(t.getMarketName().indexOf("-")+1);
			String counter = t.getMarketName().substring(0,t.getMarketName().indexOf("-"));
			String pairStr = base + "/" + counter;
			CurrencyPair cp = new CurrencyPair(pairStr);
			trexTickerMap.put(cp, t);
		}
		return trexTickerMap;
	}

	@Override
	public BigDecimal getAccountBalance(TreeMap<String, BigDecimal> currentCloseMap) {
		return null;
	}

}
