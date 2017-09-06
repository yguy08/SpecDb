package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.trade.SpecDbTrade;

public class TradeDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void updateTrades(DbConnectionEnum dbce) throws SpecDbException {
		//get open trades
		List<SpecDbTrade> openTrades = getOpenTrades(dbce);
		
		//delete open trades
		DeleteRecord.deleteTradeRecords(dbce);
		
		//update trades
		Map<String,BigDecimal> closeMap = MarketDAO.getCurrentCloseMap(dbce);
		for(SpecDbTrade sbt : openTrades) {
			String symbol = sbt.getBase()+sbt.getCounter()+":"+sbt.getExchange();
			BigDecimal currentPrice = closeMap.get(symbol);
			sbt.setCurrentPrice(currentPrice);
		}
		InsertRecord.insertUpdatedTrades(dbce,openTrades);
		
		//call dto for any trades today
		
		//combine into one trade entry...
		
		//insert into db...
	}
	
	public static List<SpecDbTrade> getOpenTrades(DbConnectionEnum dbce) throws SpecDbException {
		String sqlCommand = "SELECT * FROM Trade WHERE isOpen = true";
		List<SpecDbTrade> openTrades = QueryTable.genericTradeQuery(dbce, sqlCommand);
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getOpenTrades", "Got open trades!");
        //get bittrex balance
		return openTrades;		
	}

	public static void main(String[] args) {

		
	}
	
	

}
