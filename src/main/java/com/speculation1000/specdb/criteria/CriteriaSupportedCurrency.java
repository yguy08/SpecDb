package com.speculation1000.specdb.criteria;

import java.util.ArrayList;
import java.util.List;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.SupportedCurrencyEnum;

public class CriteriaSupportedCurrency implements Criteria {

	@Override
	public List<Market> meetCriteria(List<Market> markets) {
		List<Market> supportedMarkets = new ArrayList<>();
		
		for(Market m : markets){
			for(SupportedCurrencyEnum sce : SupportedCurrencyEnum.values()){
				if(m.getSymbol().getCounter().equalsIgnoreCase(sce.getCounter())){
					supportedMarkets.add(m);
				}
			}
		}
		return supportedMarkets;
	}

}
