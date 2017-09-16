package com.speculation1000.specdb.start;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.AccountDAO;
import com.speculation1000.specdb.dao.EntryDAO;
import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.dao.TradeDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Entry;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.time.SpecDbTime;

public class StandardMode implements Runnable {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
    private long period;
    
    private long initialDelay;

	static Instant startRunTS = null;
    
    public StandardMode(Config config){
    	if(config.getInitialDelay()){
    		initialDelay = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
    	}else{
    		initialDelay = 0;
    	}    	
    	period = config.getRunPeriod() * 60;    	
    }
    
    public StandardMode(){}

    public void startRun() {
        specLogger.logp(Level.INFO,StandardMode.class.getName(),"startApp","* Next update in " + initialDelay + " seconds");
        scheduler.scheduleAtFixedRate(new StandardMode(), initialDelay, period, SECONDS);		
    }

    public void run() {
        
    	setStartRunTS();
        
        getStartRunMessage();
                
        try{
        	new MarketDAO(DbConnectionEnum.H2_MAIN);
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error updating markets!");
        }
        
        //update account balance
        try{
        	new AccountDAO(DbConnectionEnum.H2_MAIN);
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error updating account balance!");
        }
        
        //update trades
        try{
        	new EntryDAO(DbConnectionEnum.H2_MAIN, 25);
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error updating trades!");
        }        
        
        try{
            getTickerString(DbConnectionEnum.H2_MAIN);
            getEntriesString(DbConnectionEnum.H2_MAIN);
            getBalanceString(DbConnectionEnum.H2_MAIN);
            getSystemStatus();
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error getting status messages");
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

	public static void setStartRunTS(){
		startRunTS = Instant.now();
	}

	public static void getStartRunMessage(){
	    StringBuilder sb = new StringBuilder();
	    sb.append("\n");
	    sb.append("********************************\n");
	    sb.append("          [ STANDARDMODE ] \n");
	    sb.append("********************************\n");
	    sb.append("            [START]\n");
	    sb.append("* At: ");
	    sb.append(SpecDbDate.instantToLogStringFormat(getStartRunTS()) + "\n");
	    sb.append("********************************\n");
	    specLogger.logp(Level.INFO, StartApp.class.getName(), "getStartRunMessage", sb.toString());
	}

	public static void getEndRunMessage(){
	    StringBuilder sb = new StringBuilder();
	    sb.append("\n");
	    sb.append("********************************\n");
	    sb.append("          [ STANDARDMODE ] \n");
	    Instant end = Instant.now();
	    sb.append("********************************\n");
	    sb.append("             [END]\n");
	    sb.append("* At: ");
	    sb.append(SpecDbDate.instantToLogStringFormat(end) + "\n");
	    sb.append("* Runtime: ");
	    sb.append(end.getEpochSecond() - getStartRunTS().getEpochSecond() + " sec\n");
	    sb.append("********************************\n");
	    long i = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
	    sb.append("* Next Update in " + i + " seconds\n");
	    sb.append("********************************\n");
	    specLogger.logp(Level.INFO, StandardMode.class.getName(),"getEndRunMessage", sb.toString());
	}
	
	public static void getSystemStatus(){
        StringBuilder sb = new StringBuilder();
        Instant now = Instant.now();
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [SYSTEMSTATUS] \n");
        sb.append("* At: ");
        sb.append(SpecDbDate.instantToLogStringFormat(now) + "\n");
        sb.append("* App Running since: ");
        sb.append(SpecDbDate.instantToLogStringFormat(StartApp.getStartUpTs()));
        sb.append(" (Uptime: " + SpecDbTime.uptimePrettyStr(StartApp.getSystemUptime()) + ")\n");
        sb.append("* H2 DB Running since: ");
        sb.append(SpecDbDate.instantToLogStringFormat(DbServer.DB_START_UP_TS));
        sb.append(" (Uptime: " + SpecDbTime.uptimePrettyStr(DbServer.getSystemUptime()) + ")\n");
        sb.append("* H2 DB Status: ");
        sb.append(DbServer.getH2ServerStatus() + "\n");
        sb.append("********************************\n");
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"getSystemStatus", sb.toString());
	}
	
	public static void getTickerString(DbConnectionEnum dbce){
		TreeMap<Symbol, BigDecimal> marketStatusMap = MarketDAO.getCurrentCloseMap(dbce);
	    StringBuilder sb = new StringBuilder();
	    sb.append("********************************\n");
	    sb.append("          [ TICKERTAPE ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
		for(Map.Entry<Symbol, BigDecimal> e : marketStatusMap.entrySet()){
			sb.append(e.getKey() + " @" + e.getValue()+"\n");
		}
	    sb.append("********************************\n");
	    specLogger.logp(Level.INFO, StandardMode.class.getName(),"getTickerString", sb.toString());		
	}
	
	public static void getEntriesString(DbConnectionEnum dbce){
		StringBuilder sb = new StringBuilder();
		List<Entry> marketEntryList = EntryDAO.getMarketEntryList(dbce);
	    sb.append("\n");
		sb.append("********************************\n");
	    sb.append("          [ ENTRIES ]\n");
	    sb.append(SpecDbDate.instantToShortDateTimeStr(Instant.now())+"\n");
	    marketEntryList.forEach(item->sb.append((item+"\n")));
	    sb.append("********************************\n");
	    specLogger.logp(Level.INFO, StandardMode.class.getName(),"getEntriesString", sb.toString());	
	}
	
	public static void getBalanceString(DbConnectionEnum dbce) throws SpecDbException{
		StringBuilder sb = new StringBuilder();
	    sb.append("\n");
		sb.append("********************************\n");
	    sb.append("          [ BALANCE ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
		sb.append(AccountDAO.getCurrentAccountBalance(dbce)+"\n");
	    sb.append("********************************\n");
	    specLogger.logp(Level.INFO, StandardMode.class.getName(),"getBalanceString", sb.toString());
	}
}
