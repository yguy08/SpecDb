package com.speculation1000.specdb.criteria;

import java.util.ArrayList;
import java.util.List;

import com.speculation1000.specdb.market.Market;

public class CriteriaOldMarket implements Criteria {
	
	@Override
	public List<Market> meetCriteria(List<Market> markets) {
		List<Market> oldMarkets = new ArrayList<>();
		
		for(Market market : markets){
			if(market.getHistorical().size()>=55){
				oldMarkets.add(market);
			}
		}
		return oldMarkets;
	}

}
