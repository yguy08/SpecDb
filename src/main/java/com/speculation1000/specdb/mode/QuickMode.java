package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.speculation1000.specdb.dao.BittrexDAO;
import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.dao.PoloniexDAO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbDate;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.SpecDbTime;
import com.speculation1000.specdb.start.StartRun;

public class QuickMode implements Mode {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final int PERIOD = 60 * 15;

	@Override
	public String getStartRunMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ QUICKMODE ]\n");
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
		StringJoiner sj = new StringJoiner(":", "[", "]");
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ QUICKMODE ]\n");
		Instant end = Instant.now();
		sb.append("********************************\n");
		sb.append("          [ RESULTS ]\n");
		sb.append("* Time: ");
		sb.append(end.getEpochSecond() - StartRun.getStartRunTS().getEpochSecond() + " sec\n");
		sb.append("********************************\n");
		for(Market market : MarketSummaryDAO.getLatest()){
			sj.add(market.toString());
		}
		sb.append(sj.toString() + "\n");
		sb.append("********************************\n");
		int i = SpecDbTime.getQuickModeDelay(Instant.now());
		if(i == 0){
			i = PERIOD / 60;
		}
		sb.append("* Next Update in " + i + " minutes\n");
		sb.append("********************************\n");
		return sb.toString();
	}

	@Override
	public void startApp() {
		long nextQuarterInitialDelay = SpecDbTime.getQuickModeDelay(Instant.now());
		scheduler.scheduleAtFixedRate(new QuickMode(), nextQuarterInitialDelay * 60, PERIOD, SECONDS);		
	}

	@Override
	public void run() {
		StartRun.setStartRunTS();
		specLogger.log(QuickMode.class.getName(),getStartRunMessage());
		
		PoloniexDAO polo = new PoloniexDAO();
		
		try{
			polo.updateMarkets();
		}catch(com.speculation1000.specdb.start.SpecDbException e){
			specLogger.log(QuickMode.class.getName(),e.getMessage());
		}
		
		BittrexDAO bittrex = new BittrexDAO();
		try{
			bittrex.updateMarkets();
		}catch(SpecDbException e){
			specLogger.log(QuickMode.class.getName(),e.getMessage());
		}
		
		if(SpecDbDate.isNewDay(StartRun.getStartRunTS())){
			specLogger.log(QuickMode.class.getName(),"New Day!");
		}
		
		specLogger.log(QuickMode.class.getName(),getEndRunMessage());
	}


}
