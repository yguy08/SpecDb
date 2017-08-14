package com.speculation1000.specdb.dto;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.speculation1000.specdb.market.Market;

public class PoloniexDTOTest {

	@Test
	public void testFetchExchangeHistory(){
		List<Market> marketList = new PoloniexDTO().fetchExchangeHistory(999999999L);
		for(Market market : marketList){
			assertNotNull(market.getBase());
			assertNotNull(market.getCounter());
			assertNotNull(market.getExchange());
		}
		
	}
	
}
