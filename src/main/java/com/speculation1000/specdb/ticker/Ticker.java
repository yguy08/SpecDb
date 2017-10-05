package com.speculation1000.specdb.ticker;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.speculation1000.specdb.criteria.CriteriaOldMarket;
import com.speculation1000.specdb.criteria.CriteriaSupportedCurrency;
import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.Config;

public class Ticker {
	
	public static TreeMap<Symbol,Market> ticker;

	public static TreeMap<Symbol,Market> getTicker(){
		return ticker;
	}
	
	public static void updateTicker(){
		ticker = new TreeMap<>();
		
		//
		List<Market> markets = MarketDAO.getMarketList(Config.getDatabase(), 100);
		
		markets = new CriteriaSupportedCurrency().meetCriteria(markets);
		
		markets = new CriteriaOldMarket().meetCriteria(markets);
		
		for(Market m : markets){
			ticker.put(m.getSymbol(), m);
		}
		
		for(Map.Entry<Symbol,Market> e : ticker.entrySet()){
			System.out.println(e.getValue());
		}
		
	}

}
