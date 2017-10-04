package com.speculation1000.specdb.start;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.stream.IntStream;

import com.speculation1000.specdb.criteria.CriteriaSupportedCurrency;
import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.exchange.ExchangeFcty;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.SupportedCurrencyEnum;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;
import com.speculation1000.specdb.utils.SpecDbTime;

public class StandardMode implements Runnable {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	static Instant startRunTS = null;
	
	private static StandardMode standardMode;
    
    public StandardMode(){
    	try{
        	standardMode = this;
        	long initialDelay = 0;
        	if(Config.getInitialDelay()>0) {
        		initialDelay = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
        	}
            specLogger.logp(Level.INFO,StandardMode.class.getName(),"startApp","* Next update in " + initialDelay + " seconds");
            scheduler.scheduleAtFixedRate(standardMode, initialDelay, Config.getRunPeriod() * 60, SECONDS);
    	}catch(Exception e){
    		specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"StandardMode","Standard Mode failed!");
    		throw(e);
    	}
    }

    public void run() {   	
    	try{    		
    		////////////////
        	// START RUN
    		///////////////
    		
        	setStartRunTS();    	
    		specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", "[STANDARDMODE] - @" + SpecDbDate.instantToLogStringFormat(getStartRunTS()));
    		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(getStartRunTS());
    		
    		try{
        		//Get latest markets from supported exchanges
        		specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Getting latest markets from: "+ExchangeEnum.supportedExchangesStr());
        		
        		List<Market> markets = new ArrayList<>();
        		for(ExchangeEnum exchange : ExchangeEnum.values()){
        			try {
        				markets.addAll(ExchangeFcty.getExchangeDAO(exchange).getLatestMarkets());
        				specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Got latest markets from: "+exchange.getExchangeSymbol());
        			} catch (SpecDbException e) {
        				specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"Latest markets","Failed to load markets from: "+exchange.getExchangeSymbol()+" "+e.getMessage());
        			}
        		}
        		
        		//Do not do delete or add if we failed to load markets on a run
        		if(markets.size()>0){
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Markets retrieved: " + markets.size());
        			
        			//Filter for supported currencies
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Filter markets for supported currencies: "+SupportedCurrencyEnum.supportedCurrencyStr());
        			List<Market> supportedMarkets = new CriteriaSupportedCurrency().meetCriteria(markets);
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Markets not supported: "+(markets.size()-supportedMarkets.size()));
        			
        			int sum = 0;
        			//Pulling markets every hour so clean up old ones from today
    				specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Cleaning up markets for today: " + todayMidnight);
        			int[] cleanedUp = DbUtils.marketCleanUp(todayMidnight);
        			sum = IntStream.of(cleanedUp).sum();
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Cleaned up "+sum+" markets");
        			
        			//insert new markets
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Adding new markets..");
        			sum = DbUtils.insertMarkets(Config.getDatabase(), markets);
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","Added " +sum+" new markets");   			
        		}else{
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run","No new markets loaded! Something went wrong");
        		}    			
    		}catch(Exception e){
    			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"run","Something went wrong: "+e.getMessage());
    		}
    		
    		
    		//restore any missing days in last 100
    		try{
        		for(ExchangeEnum exchange : ExchangeEnum.values()){
        			int sum = ExchangeFcty.getExchangeDAO(exchange).restoreMarkets(Config.getDatabase(),100);
        			specLogger.logp(Level.INFO,StandardMode.class.getName(),"run",exchange.getExchangeSymbol()+": restored "+sum);
        		}
    		}catch(Exception e){
    			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"run","Restore failed: "+e.getMessage());
    		}
    		
    		try{
    			//update account balance
        		List<AccountBalance> accounts = new ArrayList<>();
        		for(ExchangeEnum exchange : ExchangeEnum.values()){
        			accounts.addAll(ExchangeFcty.getExchangeDAO(exchange).getAccountBalance(Config.getDatabase()));
        		}
        		
        		if(accounts.size()>0){
        			DbUtils.accountBalCleanUp(Config.getDatabase(), todayMidnight);
        			DbUtils.insertUpdatedAccountBalances(Config.getDatabase(), accounts);
        		}
    		}catch(Exception e){
    			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"run","Failed to get account balances: "+e.getMessage());
    		}
    		
    	}catch(Exception e){
			for(StackTraceElement ste : e.getStackTrace()){
				specLogger.logp(Level.SEVERE, StartApp.class.getName(), "StartApp", ste.toString());
			}
			//next update in...
    	}
		
    }

	public static Instant getStartRunTS(){
		//For testing purposes, don't need to set start every time
		if(startRunTS != null){
			return startRunTS;
		}else{
			setStartRunTS();
			return startRunTS;
		}
	}

	private static void setStartRunTS(){
		startRunTS = Instant.now();
	}
	
	private static void getSystemStatus(){
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "********************************");
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "[SYSTEMSTATUS]");
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* At: " + SpecDbDate.instantToLogStringFormat(Instant.now()));
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* App Running since: " + SpecDbDate.instantToLogStringFormat(StartApp.getStartUpTs()));
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* App Uptime: " + SpecDbTime.uptimePrettyStr(StartApp.getSystemUptime()));
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* H2 DB Status: " + DbServer.getH2ServerStatus());
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* Total Memory (mB): " + rt.totalMemory() / 1024 / 1024);
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* Free Memory (mB): " + rt.freeMemory() / 1024 / 1024);
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* Used Memory (mB): " + usedMB);
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* Max (mB): " + rt.maxMemory() / 1024 / 1024);
	    long i = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", "* Next Update in " + i + " seconds\n");
	}
	
	public static void getTickerStatus(DbConnectionEnum dbce){
			
	}
}
