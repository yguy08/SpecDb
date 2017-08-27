package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;

public class MarketSummaryDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static long getOldestRecordByExchange(Connection connection,String exchange){
		String sqlCommand = "SELECT Min(Date) AS Date FROM markets WHERE exchange = " + "'"+exchange+"'";
		List<Market> marketList = QueryTable.genericMarketQuery(connection, sqlCommand);
		return marketList.get(0).getDate();
	}
	
	public static void updateTickerList(DbConnectionEnum dbce) throws SpecDbException{
		List<Market> poloMarket = new PoloniexDAO().getLatestMarkets();
    	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList","Updated polo successful");
		List<Market> bittrexMarkets = new BittrexDAO().getLatestMarkets();
    	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList","Updated trex successful");
    	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList","got clean up list successful");
    	long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
    	try{
    		DeleteRecord.deleteBulkMarkets(dbce, todayMidnight);
    	}catch(Exception e){
        	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList",e.getMessage());
    	}
    	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList","deleted clean up list");
		InsertRecord.insertBatchMarkets(dbce, poloMarket);
    	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList","inserted polo list");
		InsertRecord.insertBatchMarkets(dbce, bittrexMarkets);
    	specLogger.logp(Level.INFO, MarketSummaryDAO.class.getName(),"updateTickerList","inserted trex list");
	}
	
	public static List<Market> getCleanUpList(DbConnectionEnum dbce){
    	long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
		String sqlCommand = "SELECT * FROM Markets WHERE Date = " + todayMidnight + " ORDER BY Counter,Base ASC";
		Connection conn = DbConnection.connect(dbce);
		List<Market> marketList = QueryTable.genericMarketQuery(conn, sqlCommand);
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.SEVERE, MarketSummaryDAO.class.getName(), "getCleanUpList", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return marketList;
	}
	
	public static List<Market> getTickerList(DbConnectionEnum dbce){
		String sqlCommand = "SELECT * FROM Markets WHERE Date = (SELECT Max(Date) Date FROM Markets)" 
							+ " ORDER BY Counter,Base ASC";
		Connection conn = DbConnection.connect(dbce);
		List<Market> marketList = QueryTable.genericMarketQuery(conn, sqlCommand);
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.SEVERE, MarketSummaryDAO.class.getName(), "getTickerList", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return marketList;
	}
	
	public static List<Market> getMaxCloseList(DbConnectionEnum dbce, int days){
		Instant instant = Instant.now().minusSeconds(86400 * days);
		String sqlCommand = "SELECT Base,Counter,Exchange,Max(Close) Close" 
				+ " FROM Markets" 
				+ " WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant) 
				+ " GROUP BY Base,Counter,Exchange" 
				+ " ORDER BY Counter,Base ASC";
		Connection conn = DbConnection.connect(dbce);
		List<Market> marketList = QueryTable.genericMarketQuery(conn, sqlCommand);
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.SEVERE, MarketSummaryDAO.class.getName(), "getMaxCloseList", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return marketList;
	}
	
	public static List<Market> getMinCloseList(DbConnectionEnum dbce, int days){
		Instant instant = Instant.now().minusSeconds(86400 * days);
		String sqlCommand = "SELECT Base,Counter,Exchange,Min(Close) Close" 
				+ " FROM Markets" 
				+ " WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant) 
				+ " GROUP BY Base,Counter,Exchange" 
				+ " ORDER BY Counter,Base ASC";
		Connection conn = DbConnection.connect(dbce);
		List<Market> marketList = QueryTable.genericMarketQuery(conn, sqlCommand);
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.SEVERE, MarketSummaryDAO.class.getName(), "getMinCloseList", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return marketList;
	}
	
	public static List<Market> getCurrentCloseList(DbConnectionEnum dbce){
		Instant instant = Instant.now();
		String sqlCommand = "SELECT Base,Counter,Exchange,Max(Close) Close" 
				+ " FROM Markets" 
				+ " WHERE Date = " + SpecDbDate.getTodayMidnightEpochSeconds(instant) 
				+ " GROUP BY Base,Counter,Exchange" 
				+ " ORDER BY Counter,Base ASC";
		Connection conn = DbConnection.connect(dbce);
		List<Market> marketList = QueryTable.genericMarketQuery(conn, sqlCommand);
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.SEVERE, MarketSummaryDAO.class.getName(), "getCurrentCloseList", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return marketList;
	}
	
	public static List<Market> getMarketsAtXDayHigh(DbConnectionEnum dbce,int days){
		List<Market> maxCloseList = getMaxCloseList(DbConnectionEnum.H2_MAIN,25);
		List<Market> currentClose = getCurrentCloseList(DbConnectionEnum.H2_MAIN);
		List<Market> marketsAtHighList = new ArrayList<>();
		for(Market market : currentClose){
			for(Market m : maxCloseList){
				String marketStr = market.getBase()+market.getCounter()+market.getExchange();
				String mStr = m.getBase()+m.getCounter()+m.getExchange();
				if(marketStr.equalsIgnoreCase(mStr)){
					BigDecimal maxClose = m.getClose();
					BigDecimal current = market.getClose();
					if(current.compareTo(maxClose) == 0){
						marketsAtHighList.add(market);
					}
				}
			}
		}
		return marketsAtHighList; 
	}
	
	public static List<Market> getMarketsAtXDayLow(DbConnectionEnum dbce,int days){
		List<Market> minCloseList = getMinCloseList(DbConnectionEnum.H2_MAIN,25);
		List<Market> currentClose = getCurrentCloseList(DbConnectionEnum.H2_MAIN);
		List<Market> marketsAtLowList = new ArrayList<>();
		for(Market market : currentClose){
			for(Market m : minCloseList){
				String marketStr = market.getBase()+market.getCounter()+market.getExchange();
				String mStr = m.getBase()+m.getCounter()+m.getExchange();
				if(marketStr.equalsIgnoreCase(mStr)){
					BigDecimal maxClose = m.getClose();
					BigDecimal current = market.getClose();
					if(current.compareTo(maxClose) == 0){
						marketsAtLowList.add(market);
					}
				}
			}
		}
		return marketsAtLowList; 
	}
	
	public static void restoreMarkets() throws SpecDbException{
        PoloniexDAO polo = new PoloniexDAO();
    	polo.restoreMarkets();
	}

}
