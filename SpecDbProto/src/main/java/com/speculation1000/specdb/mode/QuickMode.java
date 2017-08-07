package com.speculation1000.specdb.mode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.speculation1000.specdb.SpecDbException;
import com.speculation1000.specdb.dao.BittrexDAO;
import com.speculation1000.specdb.dao.MarketSummaryList;
import com.speculation1000.specdb.dao.PoloniexDAO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.utils.SpecDbDate;

public class QuickMode implements Runnable, Mode {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
		
	private static Instant startRunTS = null;	

	@Override
	public void setStartRunTS(Instant instant) {
		startRunTS = instant;		
	}

	@Override
	public Instant getStartRunTS() {
		return startRunTS;
	}

	@Override
	public String getStartRunMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ QUICKMODE ]\n");
		sb.append("********************************\n");
		sb.append("            [ START ]\n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartRunTS()) + "\n");
		sb.append("* New Day: " + SpecDbDate.isNewDay(getStartRunTS()) + "\n");
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
		sb.append(end.getEpochSecond() - getStartRunTS().getEpochSecond() + " sec\n");
		sb.append("********************************\n");
		for(Market market : MarketSummaryList.getLatest()){
			sj.add(market.toString());
		}
		sb.append(sj.toString() + "\n");
		sb.append("********************************\n");
		long nextUpdate = 60 * 2 - (Instant.now().getEpochSecond() - getStartRunTS().getEpochSecond());
		sb.append("* Next Update in " + nextUpdate + " seconds\n");
		sb.append("********************************\n");
		return sb.toString();
	}

	@Override
	public void startApp() {
		scheduler.scheduleAtFixedRate(new QuickMode(), 1, 60 * 2, SECONDS);		
	}

	@Override
	public void run() {
		setStartRunTS(Instant.now());
		specLogger.log(QuickMode.class.getName(),getStartRunMessage());
		
		PoloniexDAO polo = new PoloniexDAO();
		
		try{
			polo.updateMarkets();
		}catch(SpecDbException e){
			specLogger.log(QuickMode.class.getName(),e.getMessage());
		}
		
		BittrexDAO bittrex = new BittrexDAO();
		try{
			bittrex.updateMarkets();
		}catch(SpecDbException e){
			specLogger.log(QuickMode.class.getName(),e.getMessage());
		}
		
		if(SpecDbDate.isNewDay(getStartRunTS())){
			
		}
		
		
		specLogger.log(QuickMode.class.getName(),getEndRunMessage());
	}


}
