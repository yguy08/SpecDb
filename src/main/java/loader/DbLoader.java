package loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.CreateMarketTable;

public class DbLoader {
	
	private static boolean netConnected;
		
	public DbLoader() {
		//create table if does not exist
		new CreateMarketTable().insertIfNotExists();
		
		testNetworkConnection();
		
		if(isConnected()){			
			//polo loader
			new PoloniexLoader();
		}else{
			System.out.println("No network connection!");
			System.out.println("Connect to a network and try again to get latest market updates.");
		}
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
	
	public static boolean isConnected() {
		return netConnected;
	}
	
	public static void main(String[] args) {
		new DbLoader();
	}
	
}
