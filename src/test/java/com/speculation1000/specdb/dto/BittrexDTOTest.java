package com.speculation1000.specdb.dto;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;

public class BittrexDTOTest {

	@Test
	public void getLatestMarketList(){
		List<Market> marketList = new ArrayList<>();
		try {
			marketList = new BittrexDTO().getLatestMarketList();
		} catch (SpecDbException e) {
			e.printStackTrace();
		}
		for(Market market : marketList){
			assertNotNull(market.getBase());
			assertNotNull(market.getCounter());
			assertNotNull(market.getExchange());
			System.out.println(market.toString());
		}
	}

}
