package com.speculation1000.specdb.dto;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;

import org.junit.Test;

import com.speculation1000.specdb.market.Market;

public class PoloniexDTOTest {

	@Test
	public void testFetchExchangeHistory(){
		Instant now = Instant.now();
		List<Market> marketList = new PoloniexDTO().fetchExchangeHistory(now.getEpochSecond());
		for(Market market : marketList){
			assertNotNull(market.getBase());
			assertNotNull(market.getCounter());
			assertNotNull(market.getExchange());
		}
		
	}
	
}
