package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

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

public class QuickMode implements Mode {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
    private static final int PERIOD = 60 * 1;
    
    private static final String modeNameStr = "QUICKMODE";

    @Override
    public void startRun() {
        scheduler.scheduleAtFixedRate(new QuickMode(), 0, PERIOD, SECONDS);		
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
        	MarketStatus.updateMarketStatusList(DbConnectionEnum.H2_MAIN);
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error updating market status list!");
        }
        
        try{
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StatusString.getTickerString());
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StartRun.getEndRunMessage());
            specLogger.logp(Level.INFO, QuickMode.class.getName(), "run", StatusString.getLongEntriesString());
            specLogger.logp(Level.INFO, QuickMode.class.getName(), "run", StatusString.getShortEntriesString());
            specLogger.logp(Level.INFO, QuickMode.class.getName(),"run",StatusString.getSystemStatus());
            specLogger.logp(Level.INFO, QuickMode.class.getName(), "run", StatusString.getBalanceStr());
        }catch(Exception e){
        	specLogger.logp(Level.SEVERE, QuickMode.class.getName(),"run","Error getting status messages");
        }
        
    }

	@Override
	public String getModeName() {
		return modeNameStr;
	}
}
