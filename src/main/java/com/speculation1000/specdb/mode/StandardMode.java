package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.BittrexDAO;
import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.dao.PoloniexDAO;
import com.speculation1000.specdb.db.DbServer;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.time.SpecDbTime;

public class StandardMode implements Mode {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
    
    private static final int PERIOD = 60 * 15;
    
    @Override
    public String getStartRunMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [ STANDARDMODE ]\n");
        sb.append("********************************\n");
        sb.append("            [ START ]\n");
        sb.append("* At: ");
        sb.append(SpecDbDate.instantToLogStringFormat(StartRun.getStartRunTS()) + "\n");
        sb.append("* New Day: " + SpecDbDate.isNewDay(StartRun.getStartRunTS()) + "\n");
        sb.append("********************************\n");
        return sb.toString();
    }
    
    @Override
    public String getEndRunMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [ STANDARDMODE ]\n");
        Instant end = Instant.now();
        sb.append("********************************\n");
        sb.append("          [ RESULTS ]\n");
        sb.append("* Time: ");
        sb.append(end.getEpochSecond() - StartRun.getStartRunTS().getEpochSecond() + " sec\n");
        sb.append("********************************\n");
        long i = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
        sb.append("* Next Update in " + i + " seconds\n");
        sb.append("* H2 Db Server Status: \n");
        sb.append("* " + DbServer.getH2ServerStatus() + " *\n");
        sb.append("********************************\n");
        return sb.toString();
    }

    @Override
    public void startApp() {
        long nextQuarterInitialDelay = SpecDbTime.getQuickModeDelaySeconds(Instant.now());
        specLogger.logp(Level.INFO,StandardMode.class.getName(),"startApp","* Next update in " + nextQuarterInitialDelay + " seconds");
        scheduler.scheduleAtFixedRate(new StandardMode(), nextQuarterInitialDelay, PERIOD, SECONDS);		
    }

    @Override
    public void run() {
        StartRun.setStartRunTS();
        specLogger.logp(Level.INFO, StandardMode.class.getName(), "run",getStartRunMessage());
        
        PoloniexDAO polo = new PoloniexDAO();
        
        try{
            polo.updateMarkets();
        }catch(com.speculation1000.specdb.start.SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(), "run",e.getMessage());
        }
        
        BittrexDAO bittrex = new BittrexDAO();
        try{
            bittrex.updateMarkets();
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(), "run",e.getMessage());
        }
        
        //new day cleanup 
        if(SpecDbDate.isNewDay(StartRun.getStartRunTS())){
            try{
            	polo.cleanUpForNewDay();
            	specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", "Polo clean up successful");
            }catch(SpecDbException e){
            	specLogger.logp(Level.SEVERE, StandardMode.class.getName(), "run", "Error during Polo clean up");
            }
            
            try{
            	bittrex.cleanUpForNewDay();
            	specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", "Trex clean up successful");
            }catch(SpecDbException e){
            	specLogger.logp(Level.SEVERE, StandardMode.class.getName(), "run", "Error during TREX clean up");
            }
        }
        
        //restore
        try{
        	polo.restoreMarkets();
        	specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", "POLO restore successful");
        }catch(SpecDbException e){
        	specLogger.logp(Level.SEVERE, StandardMode.class.getName(), "run", "Error during POLO restore");
        }
        
        specLogger.logp(Level.INFO, StandardMode.class.getName(), "run", MarketSummaryDAO.getEntryStatus());
        
        specLogger.logp(Level.INFO, StandardMode.class.getName(), "run",getEndRunMessage());
    }


}
