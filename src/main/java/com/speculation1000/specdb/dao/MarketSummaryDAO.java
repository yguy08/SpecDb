package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.mode.QuickMode;
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
    	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","Updated polo successful");
		List<Market> bittrexMarkets = new BittrexDAO().getLatestMarkets();
    	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","Updated trex successful");
    	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","got clean up list successful");
    	long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
    	try{
    		DeleteRecord.deleteBulkMarkets(dbce, todayMidnight);
    	}catch(Exception e){
        	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",e.getMessage());
    	}
    	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","deleted clean up list");
		InsertRecord.insertBatchMarkets(dbce, poloMarket);
    	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","inserted polo list");
		InsertRecord.insertBatchMarkets(dbce, bittrexMarkets);
    	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","inserted trex list");
	}
	
	public static List<Market> getCleanUpList(DbConnectionEnum dbce){
    	long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
		String sqlCommand = "SELECT * FROM Markets WHERE Date = " + todayMidnight;
		Connection conn = DbConnection.connect(dbce);
		List<Market> marketList = QueryTable.genericMarketQuery(conn, sqlCommand);
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.INFO, QueryTable.class.getName(), "getLongEntries", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return marketList;
	}
	
	public static void restoreMarkets() throws SpecDbException{
        PoloniexDAO polo = new PoloniexDAO();
    	polo.restoreMarkets();
	}
	
	public static String getEntryStatus(){
        StringBuilder sb = new StringBuilder();
        List<Market> marketList = getCleanUpList(DbConnectionEnum.H2_MAIN);
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [ TICKERTAPE ]\n");
        sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
        for(Market m : marketList){
        	sb.append(m.toString() + "\n");
        }
        sb.append("********************************\n");
        sb.append("********************************\n");
        return sb.toString();		
	}

}
