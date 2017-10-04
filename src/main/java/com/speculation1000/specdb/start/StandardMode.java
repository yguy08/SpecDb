package com.speculation1000.specdb.start;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.exchange.ExchangeFcty;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.ExchangeEnum;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;
import com.speculation1000.specdb.utils.SpecDbTime;

public class StandardMode implements Runnable {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	static Instant startRunTS = null;
    
    public StandardMode(){}

    public void startRun() {
    	long initialDelay = 0;
    	if(Config.getInitialDelay()>0) {
    		initialDelay = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
    	}
        specLogger.logp(Level.INFO,StandardMode.class.getName(),"startApp","* Next update in " + initialDelay + " seconds");
        scheduler.scheduleAtFixedRate(new StandardMode(), initialDelay, Config.getRunPeriod() * 60, SECONDS);		
    }

    public void run() {
        
    	setStartRunTS();
    	
		specLogger.logp(Level.INFO, StartApp.class.getName(), "getStartRunMessage", "[STANDARDMODE] - @" + SpecDbDate.instantToLogStringFormat(getStartRunTS()));
        
		List<Market> markets = new ArrayList<>();
		for(ExchangeEnum exchange : ExchangeEnum.values()){
			try {
				markets.addAll(ExchangeFcty.getExchangeDAO(exchange).getLatestMarkets());
			} catch (SpecDbException e) {
				e.printStackTrace();
			}
		}
		
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());

		try{
			DbUtils.marketCleanUp(DbConnectionEnum.H2_MAIN, todayMidnight);			
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList",e.getMessage());
		}
		
		try{
			DbUtils.insertMarkets(DbConnectionEnum.H2_MAIN, markets);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList",e.getMessage());
		}
		
		//restore any missing days in last 100
		for(ExchangeEnum exchange : ExchangeEnum.values()){
			try {
				ExchangeFcty.getExchangeDAO(exchange).restoreMarkets(DbConnectionEnum.H2_MAIN,100);
			} catch (SpecDbException e) {
				e.printStackTrace();
			}
		}
		
		//update account balance
		List<AccountBalance> accounts = new ArrayList<>();
		for(ExchangeEnum exchange : ExchangeEnum.values()){
			try{
				accounts.addAll(ExchangeFcty.getExchangeDAO(exchange).getAccountBalance(DbConnectionEnum.H2_MAIN));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		try{
			DbUtils.accountBalCleanUp(DbConnectionEnum.H2_MAIN, todayMidnight);
			DbUtils.insertUpdatedAccountBalances(DbConnectionEnum.H2_MAIN, accounts);
		}catch(Exception e){
			e.printStackTrace();
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
