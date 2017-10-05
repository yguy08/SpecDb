package com.speculation1000.specdb.ticker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.Config;

/**
 * Unfiltered Market Ticker
 * @author wendre01
 *
 */
public class Ticker {
	
	public static TreeMap<Symbol,Market> ticker;

	public static TreeMap<Symbol,Market> getTickerMap(){
		return ticker;
	}
	
	public static List<Market> getTickerList(){
		List<Market> markets = new ArrayList<>();
		for(Map.Entry<Symbol,Market> e : ticker.entrySet()){
			markets.add(e.getValue());
		}
		return markets;
	}
	
	public static void updateTicker(){
		ticker = new TreeMap<>();
		
		List<Market> markets = MarketDAO.getMarketList(Config.getDatabase(), 100);
		
		for(Market m : markets){
			ticker.put(m.getSymbol(), m);
		}		
	}
	
	public static BigDecimal getClose(Symbol s){
		return ticker.get(s).getClose();
	}

}
