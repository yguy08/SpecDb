package com.speculation1000.specdb.dao;

import java.util.List;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;

public interface ExchangeDAO {
	
	List<Market> getLatestMarkets() throws SpecDbException;
		
	void restoreMarkets(DbConnectionEnum dbce, int days) throws SpecDbException;
	
	List<AccountBalance> getAccountBalance(DbConnectionEnum dbce) throws SpecDbException;

}
