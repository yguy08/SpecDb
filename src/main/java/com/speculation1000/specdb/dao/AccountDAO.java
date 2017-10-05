package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import com.speculation1000.specdb.account.AccountBalance;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.utils.SpecDbLogger;

public class AccountDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public AccountDAO(DbConnectionEnum dbce) throws SpecDbException{
				
	}
	
	public static BigDecimal getCurrentAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		List<AccountBalance> accountBalList = DbUtils.getLatestAccountBalances(dbce);
		BigDecimal bal = new BigDecimal(0.00);
		List<Symbol> symbolList = AccountBalance.getSymbolsListAccBalList(accountBalList);
		TreeMap<Symbol, List<Market>> closeMap = MarketDAO.getSelectMarketMap(dbce, 0, symbolList);
		for(AccountBalance ab : accountBalList){
			if(!ab.getSymbol().getCounter().equalsIgnoreCase("BTC")){
				BigDecimal btc_price = closeMap.get(new Symbol(ab.getSymbol().getCounter(),"BTC",ab.getSymbol().getExchange())).get(0).getClose();
				BigDecimal btc_value = btc_price.multiply(ab.getAmount());
				bal = bal.add(btc_value);
			}else{
				bal = bal.add(ab.getAmount());
			}			
		}		
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");        
		return bal.setScale(8,RoundingMode.DOWN);
	}
	
	public static BigDecimal getCurrentAccountBalance(){
		DbConnectionEnum dbce = DbConnectionEnum.H2_MAIN;
		List<AccountBalance> accountBalList = new ArrayList<>();
		try {
			accountBalList = DbUtils.getLatestAccountBalances(dbce);
		} catch (SpecDbException e) {
			e.printStackTrace();
		}
		BigDecimal bal = new BigDecimal(0.00);
		List<Symbol> symbolList = AccountBalance.getSymbolsListAccBalList(accountBalList);
		TreeMap<Symbol, List<Market>> closeMap = MarketDAO.getSelectMarketMap(dbce, 0, symbolList);
		for(AccountBalance ab : accountBalList){
			if(!ab.getSymbol().getCounter().equalsIgnoreCase("BTC")){
				BigDecimal btc_price = closeMap.get(new Symbol(ab.getSymbol().getCounter(),"BTC",ab.getSymbol().getExchange())).get(0).getClose();
				BigDecimal btc_value = btc_price.multiply(ab.getAmount());
				bal = bal.add(btc_value);
			}else{
				bal = bal.add(ab.getAmount());
			}			
		}		
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");        
		return bal.setScale(8,RoundingMode.DOWN);		
	}

}
