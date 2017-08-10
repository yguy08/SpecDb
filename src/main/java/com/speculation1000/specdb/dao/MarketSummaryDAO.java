package com.speculation1000.specdb.dao;

import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;

import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.market.Market;

public class MarketSummaryDAO {
	
	public static List<Market> getAllLatest(){
		String sqlCommand = "SELECT * FROM markets WHERE date = (SELECT Max(Date) from markets) order by symbol ASC";
		List<Market> marketList = DbUtils.genericMarketQuery(sqlCommand);
		return marketList;
	}
	
	public static List<Market> getYesterdaysMarketsAfterCleanUp(Instant instant){
		long yesterday = instant.getEpochSecond();
		String sqlQuery = "SELECT * FROM markets WHERE date = " + yesterday;
		List<Market> marketList = DbUtils.genericMarketQuery(sqlQuery);
		return marketList;		
	}
	
	public static String prettyMarketString(List<Market> marketList){
		StringBuilder sb = new StringBuilder();
        for(int i = 0; i < marketList.size();i++){
        	StringJoiner sj = new StringJoiner(":", "[", "]");
        	for(int z = 0; z < 10; z++){
        		sj.add(marketList.get(i).toString());
        	}
        	sb.append(sj.toString()+"\n");
        }
		return sb.toString();
	}
	
	public static void main(String[] args){
		List<Market> marketList = MarketSummaryDAO.getAllLatest();
		System.out.println(prettyMarketString(marketList));
	}

}
