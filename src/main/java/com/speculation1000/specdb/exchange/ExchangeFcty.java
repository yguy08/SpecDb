package com.speculation1000.specdb.exchange;

import com.speculation1000.specdb.exchange.dao.BittrexDAO;
import com.speculation1000.specdb.exchange.dao.ExchangeDAO;
import com.speculation1000.specdb.exchange.dao.PoloniexDAO;

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
