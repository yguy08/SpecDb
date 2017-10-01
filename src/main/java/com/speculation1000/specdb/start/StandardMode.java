package com.speculation1000.specdb.start;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.AccountDAO;
import com.speculation1000.specdb.dao.EntryDAO;
import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
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
        	new EntryDAO(DbConnectionEnum.H2_MAIN, Config.getEntryFlag());
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error updating entries!");
        }        
        
        try{
            getTickerStatus(DbConnectionEnum.H2_MAIN);
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
