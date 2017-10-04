package com.speculation1000.specdb.exchange;

import com.speculation1000.specdb.dao.BittrexDAO;
import com.speculation1000.specdb.dao.ExchangeDAO;
import com.speculation1000.specdb.dao.PoloniexDAO;
import com.speculation1000.specdb.market.ExchangeEnum;

public class ExchangeFcty {

	public static ExchangeDAO getExchangeDAO(ExchangeEnum ee){
		switch(ee){
		case POLONIEX:
			return new PoloniexDAO();
		case BITTREX:
			return new BittrexDAO();
		default:
			return null;
		}
		
	}

}
