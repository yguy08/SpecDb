package com.speculation1000.specdb.dto;

import java.util.List;
import com.speculation1000.specdb.SpecDbException;
import com.speculation1000.specdb.market.Market;

public interface ExchangeDTO {
	
	List<Market> getLatestMarketList() throws SpecDbException;
	
	List<Market> fetchEntireExchangeHistory();
	

}
