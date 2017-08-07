package loader;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import dao.MarketDAO;
import db.DbManager;
import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class NormalMode implements Runnable, Mode {
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
		sb.append("          [ NORMALMODE ]\n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartRunTS()) + "\n");
		sb.append("* New Day: " + SpecDbDate.isNewDay() + "\n");
		sb.append("********************************\n");
		sb.append("          [ START ]\n");
		sb.append("* System Start Up: ");
		sb.append(SpecDbDate.instantToLogStringFormat(DbLoader.getStartUpTs()) + "\n");
		sb.append("* System Running for: ");
		sb.append(DbLoader.getSystemUptime() + " seconds\n");
		sb.append("********************************\n");
		sb.append("* Next Update: ");
		sb.append(SpecDbDate.nextHourInitialDelay() + " seconds\n");
		return sb.toString();
	}

	@Override
	public String getEndRunMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ RESULTS ]\n");
		int i = 0;
		for(MarketDAO market : new DbManager().getToday()){
			if(i < 10){
				sb.append("* " + market.toString() + "],");
				i++;
			}else{
				sb.append("* " + market.toString() + "]\n");
				i=0;
			}
		}
		return sb.toString();
	}

	@Override
	public void startApp() {
		scheduler.scheduleAtFixedRate(new NormalMode(), SpecDbDate.nextHourInitialDelay(), 60 * 60, SECONDS);
	}

	@Override
	public void run() {
		setStartRunTS(Instant.now());
		
		specLogger.log(QuickMode.class.getName(),getStartRunMessage());
		
		PoloniexLoader.poloUpdater();
		
		try {
			new BittrexLoader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		specLogger.log(QuickMode.class.getName(),getEndRunMessage());

		
	}

}
