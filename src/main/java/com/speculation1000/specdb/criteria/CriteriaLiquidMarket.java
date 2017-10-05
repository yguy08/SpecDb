package com.speculation1000.specdb.criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.Config;
import com.speculation1000.specdb.ticker.Ticker;

public class CriteriaLiquidMarket implements Criteria {

	@Override
	public List<Market> meetCriteria(List<Market> markets) {
		List<Market> liquidMarkets = new ArrayList<>();
		
		for(Market market : markets){
			List<Market> historical = market.getHistorical();
			String counter = market.getSymbol().getCounter();
			
			BigDecimal multiplier = (counter.equalsIgnoreCase("USD") || counter.equalsIgnoreCase("USDT")) 
																	 ? new BigDecimal(1.00)
                                                                     : Ticker.getClose(new Symbol(counter,"USDT",market.getSymbol().getExchange()));
			
			List<BigDecimal> dayVolume = new ArrayList<>();
			historical.forEach(item->{
				dayVolume.add(new BigDecimal(item.getVolume()).multiply(multiplier));
			});
			
			Double average = dayVolume.stream().mapToDouble(BigDecimal::doubleValue).average().getAsDouble();
			if(average >= Config.getVolLimit()){
				liquidMarkets.add(market);
			}
			
		}
		return liquidMarkets;
	}
	
	

}
