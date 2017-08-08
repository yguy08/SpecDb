package com.speculation1000.specdb.dao;

import java.util.List;

import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.market.Market;

public class MarketSummaryList {
	
	public static List<Market> getLatest(){
		String sqlCommand = "SELECT * from markets where date = (SELECT Max(Date) from markets)";
		List<Market> marketList = DbUtils.genericMarketQuery(sqlCommand);
		return marketList;
	}
	

}
