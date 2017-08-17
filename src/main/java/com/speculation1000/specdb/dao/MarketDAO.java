package com.speculation1000.specdb.dao;

import com.speculation1000.specdb.start.SpecDbException;

public interface MarketDAO {
	
	void updateMarkets() throws SpecDbException;
	
	int cleanUpForToday() throws SpecDbException;
	
	int cleanUpForNewDay() throws SpecDbException;
		
	void restoreMarkets() throws SpecDbException;

}
