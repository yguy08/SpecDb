package com.speculation1000.specdb.dto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexTicker;
import org.knowm.xchange.bittrex.service.BittrexMarketDataService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.account.AccountService;

import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.Config;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.time.SpecDbDate;

public class BittrexDTO implements ExchangeDTO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static Exchange trexDefault; 
	
	private static Exchange trexAuthenticated;
	
	static{
		try{
			trexDefault	= ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
			trexAuthenticated	= initAuthenticatedExchange();
		}catch(Exception e){
			try {
				throw new Exception(e.getMessage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private static Exchange initAuthenticatedExchange() {
		  ExchangeSpecification spec = new ExchangeSpecification(BittrexExchange.class);
	      Config config = new Config();
		  spec.setApiKey(config.getTrexKey());
		  spec.setSecretKey(config.getTrexSecret());
		  return ExchangeFactory.INSTANCE.createExchange(spec);
	}

	@Override
	public List<Market> getLatestMarketList() throws SpecDbException {
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(StandardMode.getStartRunTS());
		Map<CurrencyPair, BittrexTicker> bittrexChartData = getBittrexChartData();
		
		List<Market> marketList = new ArrayList<>();
		for(Map.Entry<CurrencyPair, BittrexTicker> e : bittrexChartData.entrySet()){
				Market market = new Market();
				BittrexTicker t = e.getValue();
				market.setBase(e.getKey().base.toString());
				market.setCounter(e.getKey().counter.toString());
				market.setExchange(ExchangeEnum.BITTREX.getExchangeSymbol());
				market.setDate(todayMidnight);
				market.setHigh(t.getHigh());
				market.setLow(t.getLow());
				market.setClose(t.getLast());
				market.setVolume(t.getBaseVolume().intValue());
				marketList.add(market);
		}
		specLogger.logp(Level.INFO, BittrexDTO.class.getName(), "getLatestMarketList", "Got Bittrex Chart Data");
		return marketList;
	}

	@Override
	public List<Market> fetchExchangeHistory(long endDate) {
		//TODO - https://bittrex.com/Api/v2.0/pub/market/GetTicks?marketName=BTC-WAVES&tickInterval=day&_=1499127220008
		return null;
	}
	
	private Map<CurrencyPair, BittrexTicker> getBittrexChartData() throws SpecDbException {
		if(trexDefault == null){
			throw new SpecDbException("Trex exchange is not initialized");
		}
		
		BittrexMarketDataService bmds = (BittrexMarketDataService) trexDefault.getMarketDataService();
		List<BittrexTicker> tickerList = new ArrayList<>();
		
		try {
			tickerList = bmds.getBittrexTickers();
		} catch (IOException e) {
			throw new SpecDbException("Failed getting bittrex tickers");
		}
		
		Map<CurrencyPair,BittrexTicker> trexTickerMap = new HashMap<>();
		for(BittrexTicker t : tickerList){
			//trex is opposite
			String base = t.getMarketName().substring(t.getMarketName().indexOf("-")+1);
			String counter = t.getMarketName().substring(0,t.getMarketName().indexOf("-"));
			String pairStr = base + "/" + counter;
			CurrencyPair cp = new CurrencyPair(pairStr);
			trexTickerMap.put(cp, t);
		}
		return trexTickerMap;
	}

	@Override
	public List<AccountBalance> getAccountBalances() throws SpecDbException {
		if(trexAuthenticated == null){
			throw new SpecDbException("Trex exchange is not initialized");		
		}
		
		AccountService accountService = trexAuthenticated.getAccountService();
		Map<Currency, Balance> balances = new TreeMap<>();
		
		try {
			balances = accountService.getAccountInfo().getWallet().getBalances();
		} catch (NotAvailableFromExchangeException e1) {
			throw new SpecDbException("NotAvailableFromExchangeException " + e1.getMessage());
		} catch (NotYetImplementedForExchangeException e1) {
			throw new SpecDbException("NotYetImplementedForExchangeException " + e1.getMessage());
		} catch (ExchangeException e1) {
			throw new SpecDbException("ExchangeException " + e1.getMessage());
		} catch (IOException e1) {
			throw new SpecDbException("IOException " + e1.getMessage());
		}
		
		List<AccountBalance> balanceList = new ArrayList<>();
		AccountBalance balance;
		for(Map.Entry<Currency, Balance> b : balances.entrySet()){
			if(b.getValue().getTotal().compareTo(new BigDecimal(0.00)) > 0) {
				balance = new AccountBalance(SpecDbDate.getTodayMidnightEpochSeconds(StandardMode.getStartRunTS()),
											b.getKey().toString(),"TREX",b.getValue().getTotal());
				balanceList.add(balance);
			}
		}
		return balanceList;
	}

}
