package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

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

public class QuickMode implements Mode {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
    private static final int PERIOD = 60 * 15;
    
    private static final String modeNameStr = "QUICKMODE";

    @Override
    public void startRun() {
        scheduler.scheduleAtFixedRate(new QuickMode(), 0, PERIOD, SECONDS);		
    }

    @Override
    public void run() {
        StartRun.setStartRunTS();
        specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StartRun.getStartRunMessage());
        
        PoloniexDAO polo = new PoloniexDAO();
        
        try{
            polo.updateMarkets();
        }catch(com.speculation1000.specdb.start.SpecDbException e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run",e.getMessage());
        }
        
        BittrexDAO bittrex = new BittrexDAO();
        try{
            bittrex.updateMarkets();
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run",e.getMessage());
        }
        
        if(SpecDbDate.isNewHour(StartRun.getStartRunTS())){
            try{
            	polo.cleanUpForNewDay();
            	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","Polo clean up successful");
            }catch(SpecDbException e){
            	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error during Polo clean up");
            }
            
            try{
            	bittrex.cleanUpForNewDay();
            	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","Trex clean up successful");
            }catch(SpecDbException e){
            	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error during TREX clean up");
            }
        }
        
        //restore
        try{
        	polo.restoreMarkets();
        	specLogger.logp(Level.INFO, QuickMode.class.getName(),"run","POLO restore successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error during POLO restore");
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",MarketSummaryDAO.getEntryStatus());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error getting entry status");
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StartRun.getEndRunMessage());
        }catch(Exception e){
        	
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",SystemStatus.getSystemStatus());
        }catch(Exception e){
        	
        }
        
    }

	@Override
	public String getModeName() {
		return modeNameStr;
	}
}
