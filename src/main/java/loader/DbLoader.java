package loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import market.CreateMarketTable;
import market.DbInitialize;
import market.FetchNewMarketRecords;
import market.UpdateMarketTable;
import utils.SelectUtils;

public class DbLoader {
	
	private static boolean netConnected;
		
	public DbLoader() {
		//test connection
		testNetworkConnection();
		
		//create table if does not exist
		new CreateMarketTable().insertIfNotExists();
		
		//get last update date
		long lastUpdateDate = new SelectUtils().selectLastUpdate();
		System.out.println("Db populated up to " + lastUpdateDate);
        
        if(lastUpdateDate > 0){
        	System.out.println("Updating last update date " + lastUpdateDate);
        	new UpdateMarketTable(lastUpdateDate);
        	System.out.println("Updated complete...");
        	
        	long daysMissing = (new Date().getTime() / 1000 / 24 / 60 / 60) - (lastUpdateDate / 1000 / 24 / 60 / 60);
    		System.out.println("Days missing data: " + daysMissing);
	        
	        if(daysMissing > 0){
	        	System.out.println("Need next " + ((new Date().getTime() - lastUpdateDate) / 24 * 60 * 60) + " day data");
    			new FetchNewMarketRecords(lastUpdateDate).fetchNewRecords();
    	        System.out.println("Done...");
	        }else{
	        	System.out.println("Already up to date. Skipping fetch... ");
	        }
        }else{
    		System.out.println("Db full init required.");
			new DbInitialize();
        }
		System.out.println("Db loader complete.");
	}
	
	public static void main(String[] args) {
		new DbLoader();
	}
	
	public static boolean isConnected() {
		return netConnected;
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
