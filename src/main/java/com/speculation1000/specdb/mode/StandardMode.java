package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.MarketStatus;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.start.StatusString;
import com.speculation1000.specdb.time.SpecDbTime;

public class StandardMode implements Mode {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
    private static final int PERIOD = 60 * 15;
    
    private static final String modeNameStr = "STANDARDMODE";

    @Override
    public void startRun() {
        long nextQuarterInitialDelay = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
        specLogger.logp(Level.INFO,StandardMode.class.getName(),"startApp","* Next update in " + nextQuarterInitialDelay + " seconds");
        scheduler.scheduleAtFixedRate(new StandardMode(), nextQuarterInitialDelay, PERIOD, SECONDS);		
    }

    @Override
    public void run() {
        StartRun.setStartRunTS();
        specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StartRun.getStartRunMessage());
                
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
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error updating market status list!");
        }
        
        try{
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StatusString.getTickerString());
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StartRun.getEndRunMessage());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getLongEntriesString());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getShortEntriesString());
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StatusString.getSystemStatus());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getBalanceStr());
            specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", StatusString.getOpenTradesStr());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error getting status messages");
        }
    }

	@Override
	public String getModeName() {
		return modeNameStr;
	}
}
