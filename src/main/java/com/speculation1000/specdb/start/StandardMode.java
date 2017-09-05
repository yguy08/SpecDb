package com.speculation1000.specdb.start;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.AccountDAO;
import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
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
    
    public StandardMode(){
    	
    }

    public void startRun() {
        specLogger.logp(Level.INFO,StandardMode.class.getName(),"startApp","* Next update in " + initialDelay + " seconds");
        scheduler.scheduleAtFixedRate(new StandardMode(), initialDelay, period, SECONDS);		
    }

    public void run() {
        
    	setStartRunTS();
        
        getStartRunMessage();
                
        try{
        	MarketSummaryDAO.updateTickerList(DbConnectionEnum.H2_MAIN);
        	specLogger.logp(Level.INFO, StandardMode.class.getName(),"run","update successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run",e.getMessage());
        }
        
        //restore
        try{
        	MarketSummaryDAO.restoreMarkets();
        	specLogger.logp(Level.INFO, StandardMode.class.getName(),"run","Restore successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error during restore");
        }
        
        try{
        	MarketStatus.updateMarketStatusList(DbConnectionEnum.H2_MAIN);
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error updating market status list!");
        }
        
        //update account balance
        try{
        	AccountDAO.updateAccountBalance(DbConnectionEnum.H2_MAIN);
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error updating account balance!");
        }
        
        try{
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StatusString.getTickerString());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getLongEntriesString());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getShortEntriesString());
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StatusString.getSystemStatus());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getBalanceStr());
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
	    specLogger.logp(Level.INFO, StandardMode.class.getName(),"run", sb.toString());
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
}
