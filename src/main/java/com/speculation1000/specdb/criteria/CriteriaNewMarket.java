package com.speculation1000.specdb.criteria;

import java.util.ArrayList;
import java.util.List;

import com.speculation1000.specdb.market.Market;

public class CriteriaNewMarket implements Criteria {

	@Override
	public List<Market> meetCriteria(List<Market> markets) {
		List<Market> newMarkets = new ArrayList<>();
		
		for(Market market : markets){
			if(market.getHistorical().size()<55){
				newMarkets.add(market);
			}
		}
		return newMarkets;
	}

}
