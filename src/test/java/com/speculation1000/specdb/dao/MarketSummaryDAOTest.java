package com.speculation1000.specdb.dao;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.speculation1000.specdb.market.Market;

public class MarketSummaryDAOTest {

	@Test
	public void testGetLongEntries(){
		List<Market> longEntriesList = MarketSummaryDAO.getLongEntries(25);
		for(Market market : longEntriesList){
			BigDecimal maxClose = MarketSummaryDAO.getMaxClose(market.getBase(), market.getCounter(),
																market.getExchange(), 25);
			assertEquals(maxClose, market.getClose());
		}
	}
	
	@Test
	public void testGetShortEntries(){
		List<Market> shortEntriesList = MarketSummaryDAO.getShortEntries(25);
		for(Market market : shortEntriesList){
			BigDecimal minClose = MarketSummaryDAO.getMinClose(market.getBase(), market.getCounter(),
																market.getExchange(), 25);
			assertEquals(minClose, market.getClose());
		}
	}

}
