package loader;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class DbLoader implements Runnable {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static boolean netConnected;
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant START_UP_TS = Instant.now();
	
	private static Instant currentUpdateTS = null;
	
	public static void main(String[] args) {
	    new DbLoader().startApp();
	}
	
	public void startApp(){
		scheduler.scheduleAtFixedRate(new DbLoader(), 10, 60, SECONDS);
		specLogger.log(DbLoader.class.getName(), startUpStatusMessage());
	}

	@Override
	public void run() {
		//Set TS for the update about to start
		setCurrentUpdateTS(Instant.now());
		
		//Log update status message with system stats
		specLogger.log(DbLoader.class.getName(), updateStatusMessage());
		
	}
	
	private static void setCurrentUpdateTS(Instant now){
		currentUpdateTS = now;
	}
	
	//Return the current update TS
	public static Instant getCurrentUpdateTS(){
		if(currentUpdateTS == null){
			return Instant.now();
		}else{
			return currentUpdateTS;
		}
	}
	
	public static boolean isConnected() {
		return netConnected;
	}

	public static Instant getStartUpTs() {
		return START_UP_TS;
	}
	
	/*
	 * @returns int hours of system uptime
	 */
	public static long getSystemUptime(){
		return DbLoader.getCurrentUpdateTS().getEpochSecond() - DbLoader.getStartUpTs().getEpochSecond();
	}
	
	private static String startUpStatusMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********** SpecDb ************** \n");
		sb.append("[ Digital Asset Price Database ]\n");
		sb.append("********************************\n");
		sb.append("* Start Up: \n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartUpTs()) + "\n");
		sb.append("********************************\n");
		sb.append("          [ INFO ]\n");
		sb.append("* Updates every hour\n");
		sb.append("* New day starts at Zulu Midnight\n");
		sb.append("* Supported Exchanges: \n");
		sb.append("\t" + "- Poloniex\n");
		sb.append("\t" + "- Bittrex\n");
		sb.append("* Next update in: " + SpecDbDate.nextHourInitialDelay() / 60 + " minutes\n");
		sb.append("********************************\n");
		return sb.toString();
	}
	
	private static String updateStatusMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ UPDATE ]\n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getCurrentUpdateTS()) + "\n");
		sb.append("* New Day: " + SpecDbDate.isNewDay() + "\n");
		sb.append("********************************\n");
		sb.append("          [ STATS ]\n");
		sb.append("* System Start Up: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartUpTs()) + "\n");
		sb.append("* System Running for: ");
		sb.append(getSystemUptime() + " seconds\n");
		sb.append("********************************\n");
		return sb.toString();
	}

	private void testNetworkConnection() {
		final Runnable stuffToDo = new Thread() {
			  @Override 
			  public void run() { 
				try {
		    	    URL myURL = new URL("http://poloniex.com/api");
		    	    URLConnection myURLConnection = myURL.openConnection();
		    	    myURLConnection.connect();
		    	    netConnected = true;
		    	} 
		    	catch (IOException e) { 
		    		netConnected = false;
		    	}
			  }
			};

			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Future<?> future = executor.submit(stuffToDo);
			executor.shutdown();

			try { 
			  future.get(2, TimeUnit.SECONDS); 
			}
			catch (InterruptedException | TimeoutException | ExecutionException ie) { 
				netConnected = false;
			}
			if (!executor.isTerminated()){
				executor.shutdownNow();
			}
	}
}
