package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.time.Instant;
import java.util.List;

import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.time.SpecDbDate;

public class MarketSummaryDAO {
	
	public static List<Market> getAllLatest(){
		String sqlCommand = "SELECT * FROM markets WHERE date = (SELECT Max(Date) from markets) order by base ASC";
		List<Market> marketList = QueryTable.genericMarketQuery(sqlCommand);
		return marketList;
	}
	
	public static long getOldestRecordByExchange(Connection connection,String exchange){
		String sqlCommand = "SELECT Min(Date) AS Date FROM markets WHERE exchange = " + "'"+exchange+"'";
		List<Market> marketList = QueryTable.genericMarketQuery(connection, sqlCommand);
		return marketList.get(0).getDate();
	}
	
	public static List<Market> getLongEntries(int entryFlag){
		long fromDate = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now()) - 86400 * entryFlag;
		String lastXDaysQuery;
		List<Market> marketList = getLastXDays(5);
		return marketList;
	}
	
	public static List<Market> getLastXDays(int xDays){
		long fromDate = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now()) - 86400 * xDays;
		String lastXDaysSql = "SELECT * from markers where date > " + fromDate;
		return null;
	}
	
	public static List<Market> getShortEntries(int entryFlag){
		long fromDate = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now()) - 86400 * entryFlag;
		String sqlCommand = "SELECT m.* FROM markets m INNER JOIN " 
							+ "(SELECT Base,Counter, Exchange, Min(Close) Close FROM markets WHERE date > " + fromDate + " "
							+ "GROUP BY Base,Counter,Exchange) t ON m.Base = t.Base "
							+ "AND m.Counter = t.Counter AND m.exchange = t.Exchange AND m.Close >= t.Close "
							+ "WHERE date = (SELECT Max(Date) from markets)";
		List<Market> marketList = QueryTable.genericMarketQuery(sqlCommand);
		return marketList;
	}
	
	public static void main(String[] args){
		List<Market> marketList = getLongEntries(25);
		for(Market m : marketList){
			System.out.println(m.toString());
		}
	}

}
