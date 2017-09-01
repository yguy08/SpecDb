package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.MarketStatusContent;
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
	
	public static List<Market> getLastXDayList(DbConnectionEnum dbce, int days){
		Instant instant = Instant.now().minusSeconds(86400 * days);
		String sqlCommand = "SELECT * FROM Markets WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
				+ " GROUP BY Base,Counter,Exchange,Date"
				+ " ORDER BY Counter,Base ASC, Date DESC";
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
		Collections.sort(marketList);
		return marketList;
	}
	
	public static void restoreMarkets() throws SpecDbException{
        PoloniexDAO polo = new PoloniexDAO();
    	polo.restoreMarkets();
	}
	
	public static List<Market> getDistinctMarkets(DbConnectionEnum dbce){
		String sqlCommand = "SELECT DISTINCT Base,Counter,Exchange FROM MARKETS order by counter,base ASC";
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
	
	public static List<MarketStatusContent> getMarketStatusList(DbConnectionEnum dbce){
		List<Market> distinctList = getDistinctMarkets(dbce);
		List<Market> marketList = getLastXDayList(dbce,250);
		Map<String,List<Market>> marketMap = new HashMap<>();
		for(Market m : distinctList){
			List<Market> tmpList = new ArrayList<>();
			marketMap.put(m.getSymbol(), tmpList);
			for(Market m2 : marketList){
				if(m.getSymbol().equalsIgnoreCase(m2.getSymbol())){
					tmpList.add(m2);
				}else if(tmpList.size()>0){
					break;
				}
			}
		}
		List<MarketStatusContent> marketStatusList = new ArrayList<>();
		for(Map.Entry<String, List<Market>> e : marketMap.entrySet()){
			MarketStatusContent ms = new MarketStatusContent(e.getKey(),e.getValue());
			marketStatusList.add(ms);
		}
		return marketStatusList;		
	}

}
