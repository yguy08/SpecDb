package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexTicker;
import org.knowm.xchange.bittrex.v1.service.BittrexMarketDataService;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.utils.SpecDbDate;

public class BittrexDTO implements ExchangeDTO {

	@Override
	public List<Market> getLatestMarketList() {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
		BittrexMarketDataService bmds = (BittrexMarketDataService) exchange.getMarketDataService();
		
		List<BittrexTicker> tickerList = new ArrayList<>();
		
		try {
			tickerList = bmds.getBittrexTickers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Market> marketList = new ArrayList<>();
		for(BittrexTicker bt : tickerList){
			Market market = new Market();
			market.setSymbol(bt.getMarketName());
			market.setExchange("TREX");
			market.setDate(SpecDbDate.getTodayUtcEpochSeconds());
			market.setHigh(bt.getHigh());
			market.setLow(bt.getLow());
			market.setOpen(bt.getPrevDay());
			market.setClose(bt.getLast());
			market.setVolume(bt.getBaseVolume().intValue());
			marketList.add(market);
		}
		
		return marketList;
	}

	@Override
	public List<Market> fetchEntireExchangeHistory() {
		// TODO Auto-generated method stub
		return null;
	}

}
