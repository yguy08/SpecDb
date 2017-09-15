package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.trade.TradeStatusEnum;

public class TradeDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public TradeDAO(DbConnectionEnum dbce, int days) throws SpecDbException {
		//update entries
		try{
			updateTrades(dbce, days);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"TradeDAO","Error updating entries");
			throw new SpecDbException(e.getMessage());
		}
	}
	
	private void updateTrades(DbConnectionEnum dbce, int days) throws SpecDbException {
		BigDecimal accountBalance = AccountDAO.getCurrentAccountBalance(dbce);
		
		List<Market> marketList = new ArrayList<>();
		Market market;
		try{
			List<Symbol> symbolList = DbUtils.getMarketHighs(dbce, days).stream().map(Market::getSymbol).collect(Collectors.toList());
			TreeMap<Symbol,List<Market>> marketMap = MarketDAO.getSelectMarketMap(dbce, 150, symbolList);
			for(Map.Entry<Symbol, List<Market>> e : marketMap.entrySet()){
				market = Market.createNewMarketTrade(e.getKey(), e.getValue(),accountBalance,TradeStatusEnum.LONG);
				marketList.add(market);
			}
			specLogger.logp(Level.INFO, TradeDAO.class.getName(),"TradeDAO","Found new trades (long)");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"updateTrades","Error getting market highs");
		}
		
		try{
			List<Symbol> symbolList = DbUtils.getMarketLows(dbce, days).stream().map(Market::getSymbol).collect(Collectors.toList());
			TreeMap<Symbol,List<Market>> marketMap = MarketDAO.getSelectMarketMap(dbce, 150, symbolList);
			for(Map.Entry<Symbol, List<Market>> e : marketMap.entrySet()){
				market = Market.createNewMarketTrade(e.getKey(), e.getValue(),accountBalance,TradeStatusEnum.SHORT);
				marketList.add(market);
			}
			specLogger.logp(Level.INFO, TradeDAO.class.getName(),"TradeDAO","Found new trades (short)");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"updateTrades","Error getting market lows");
		}		
		
		try{
			int[] numDeleted = DbUtils.deleteNewTrades(dbce,SpecDbDate.getTodayMidnightEpochSeconds(Instant.now()));
			int sum = IntStream.of(numDeleted).sum();
			specLogger.logp(Level.INFO, TradeDAO.class.getName(),"TradeDAO","Cleaned up today's new trades (Deleted: "+sum+")");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateEntries","Error cleaning up today's entries");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			DbUtils.insertNewTrades(dbce, marketList);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateEntries","Error inserting new polo entries");
			throw new SpecDbException(e.getMessage());
		}
		
	}
	
	public static List<Market> getMarketEntryList(DbConnectionEnum dbce){
		List<Market> marketList = DbUtils.getNewTrades(dbce);
		Collections.sort(marketList);
		return marketList;
	}
	
	

}
