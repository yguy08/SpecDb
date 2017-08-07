package com.speculation1000.specdb.dto;

import java.util.List;
import java.util.Map;

import com.speculation1000.specdb.market.Market;

public interface ExchangeDTO {
	
	List<Market> getLatestMarketList();
	
	List<Market> fetchEntireExchangeHistory();
	

}
