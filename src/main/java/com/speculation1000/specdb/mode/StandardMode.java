package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.start.SystemStatus;
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
        specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StartRun.getStartRunMessage());
                
        try{
        	MarketSummaryDAO.updateTickerList(DbConnectionEnum.H2_MAIN);
        	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","update successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run",e.getMessage());
        }
        
        //restore
        try{
        	MarketSummaryDAO.restoreMarkets();
        	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","Restore successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error during restore");
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",MarketSummaryDAO.getEntryStatus());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error getting entry status");
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StartRun.getEndRunMessage());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error getting end run status");
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",SystemStatus.getSystemStatus());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error getting system status");
        }
    }

	@Override
	public String getModeName() {
		return modeNameStr;
	}
}
