package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.time.SpecDbDate;

public class AccountDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static BigDecimal accountBal;
	
	public AccountDAO(DbConnectionEnum dbce) throws SpecDbException{
		//update account balance
		try{
			updateAccountBalance(dbce);
	    	specLogger.logp(Level.INFO, AccountDAO.class.getName(),"AccountDAO","Acocunt balance update successfully!");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, AccountDAO.class.getName(),"AccountDAO","Error updating account balance");
			throw new SpecDbException(e.getMessage());
		}		
	}
	
	public void updateAccountBalance(DbConnectionEnum dbce) throws SpecDbException{
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(StandardMode.getStartRunTS());
		List<AccountBalance> balanceList = new ArrayList<>();
		//Poloniex
		try{
			balanceList.addAll(new PoloniexDAO().getAccountBalance(dbce));
			specLogger.logp(Level.INFO, AccountDAO.class.getName(),"updateAccountBalance","Got polo balances");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, AccountDAO.class.getName(),"AccountDAO","Error updating polo account balance");
			throw new SpecDbException(e.getMessage());
		}
		
		//Bittrex
		
		
		//Clean up old ones from today
		DbUtils.accountBalCleanUp(dbce,todayMidnight);
		
		//Insert updated balances
		DbUtils.insertUpdatedAccountBalances(dbce,balanceList);
		
		//update static variable
		List<AccountBalance> accountBalList = DbUtils.getLatestAccountBalances(dbce);
		BigDecimal bal = new BigDecimal(0.00);
		List<Symbol> symbolList = AccountBalance.getSymbolsListAccBalList(accountBalList);
		TreeMap<Symbol, List<Market>> closeMap = MarketDAO.getSelectMarketMap(dbce, 0, symbolList);
		for(AccountBalance ab : accountBalList){
			if(!ab.getCounter().equalsIgnoreCase("BTC")){
				BigDecimal btc_price = closeMap.get(new Symbol(ab.getCounter(),"BTC",ab.getExchange())).get(0).getClose();
				BigDecimal btc_value = btc_price.multiply(ab.getAmount());
				bal = bal.add(btc_value);
			}else{
				bal = bal.add(ab.getAmount());
			}			
		}
		accountBal = bal;
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Updated account balance!");		
	}
	
	public static BigDecimal getCurrentAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		List<AccountBalance> accountBalList = DbUtils.getLatestAccountBalances(dbce);
		BigDecimal bal = new BigDecimal(0.00);
		List<Symbol> symbolList = AccountBalance.getSymbolsListAccBalList(accountBalList);
		TreeMap<Symbol, List<Market>> closeMap = MarketDAO.getSelectMarketMap(dbce, 0, symbolList);
		for(AccountBalance ab : accountBalList){
			if(!ab.getCounter().equalsIgnoreCase("BTC")){
				BigDecimal btc_price = closeMap.get(new Symbol(ab.getCounter(),"BTC",ab.getExchange())).get(0).getClose();
				BigDecimal btc_value = btc_price.multiply(ab.getAmount());
				bal = bal.add(btc_value);
			}else{
				bal = bal.add(ab.getAmount());
			}			
		}		
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");        
		return bal;
	}
	
	public static BigDecimal getCurrentAccountBalance(){
		return accountBal;		
	}

}
