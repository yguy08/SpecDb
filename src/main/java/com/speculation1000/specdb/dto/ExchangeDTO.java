package com.speculation1000.specdb.dto;

import java.util.List;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;

public interface ExchangeDTO {
	
	List<Market> getLatestMarketList() throws SpecDbException;
	
	List<Market> fetchExchangeHistory(long endDate);
	

}
