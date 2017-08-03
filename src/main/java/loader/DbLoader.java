package loader;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import db.DbManager;
import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class DbLoader implements Runnable {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static boolean netConnected;
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static boolean isConnected() {
		return netConnected;
	}
	
	public static void main(String[] args) {
		specLogger.log(DbLoader.class.getName(), "Start up");
	    new DbLoader().updateEveryHour();
		//new DbLoader().run();
	}
	
	public void updateEveryHour(){
		long initialDelay = SpecDbDate.nextHourInitialDelay();
		scheduler.scheduleAtFixedRate(new DbLoader(), initialDelay, 60 * 60, SECONDS);
		specLogger.log(DbLoader.class.getName(), "Starting updates in " + initialDelay / 60);
	}

	@Override
	public void run() {
		System.out.println("Start: " + ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Etc/UTC")));
		new DbManager().createTable();
		
		testNetworkConnection();
		
		new PoloniexLoader();
		
		if(isConnected()){			
			try{
				PoloniexLoader.poloUpdater();
			}catch(Exception e){
				specLogger.log(DbLoader.class.getName(), "Poloniex failed...Trying Bittrex");
			}
			
			try{
				new BittrexLoader();
			}catch(Exception e){
				System.out.println("Bittrex failed..");
				specLogger.log(DbLoader.class.getName(), "Poloniex failed...Trying Bittrex");
			}
			
			System.out.println("Done...");
			specLogger.log(DbLoader.class.getName(), "Poloniex failed...Trying Bittrex");
			
		}else{
			specLogger.log(DbLoader.class.getName(), "No network connection!");
			specLogger.log(DbLoader.class.getName(), "Connect to a network and try again to get latest market updates.");
		}
		
		System.out.println("End: " + ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Etc/UTC")));
		long initialDelay = SpecDbDate.nextHourInitialDelay();
		specLogger.log(DbLoader.class.getName(), "Next update: " + initialDelay / 60 + " Minutes");
	}
	
	private void testNetworkConnection() {
		final Runnable stuffToDo = new Thread() {
			  @Override 
			  public void run() { 
				try {
		    	    URL myURL = new URL("http://poloniex.com/");
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
