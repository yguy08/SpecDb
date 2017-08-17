package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.BittrexDAO;
import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.dao.PoloniexDAO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.start.SystemStatus;
import com.speculation1000.specdb.time.SpecDbDate;
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
        
        PoloniexDAO polo = new PoloniexDAO();
        
        try{
            polo.updateMarkets();
        }catch(com.speculation1000.specdb.start.SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run",e.getMessage());
        }
        
        BittrexDAO bittrex = new BittrexDAO();
        try{
            bittrex.updateMarkets();
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run",e.getMessage());
        }
        
        try{
        	polo.cleanUpForToday();
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run",e.getMessage());
        }
        
        
        if(SpecDbDate.isNewDay(StartRun.getStartRunTS())){
            try{
            	polo.cleanUpForNewDay();
            	specLogger.logp(Level.INFO, StandardMode.class.getName(),"run","Polo clean up successful");
            }catch(SpecDbException e){
            	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error during Polo clean up");
            }
            
            try{
            	bittrex.cleanUpForNewDay();
            	specLogger.logp(Level.INFO, StandardMode.class.getName(),"run","Trex clean up successful");
            }catch(SpecDbException e){
            	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error during TREX clean up");
            }
        }
        
        //restore
        try{
        	polo.restoreMarkets();
        	specLogger.logp(Level.INFO, StandardMode.class.getName(),"run","POLO restore successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error during POLO restore");
        }
        
        //entry status
        try{
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",MarketSummaryDAO.getEntryStatus());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(),"run","Error getting entry status");
        }
        
        //end run message
        try{
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",StartRun.getEndRunMessage());
        }catch(Exception e){
        	
        }
        
        //system status
        try{
            specLogger.logp(Level.INFO, StandardMode.class.getName(),"run",SystemStatus.getSystemStatus());
        }catch(Exception e){
        	
        }
    }

	@Override
	public String getModeName() {
		return modeNameStr;
	}
}
