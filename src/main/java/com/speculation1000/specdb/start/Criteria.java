package com.speculation1000.specdb.start;

import java.util.List;

import com.speculation1000.specdb.market.Market;

public interface Criteria {
	   
	public List<Market> meetCriteria(List<Market> market);

}
