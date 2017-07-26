package loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import market.CreateMarketTable;

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
			long lastUpdate = lastUpdate();
			if(lastUpdate > 0){
				System.out.println("Markets populated up to: " + lastUpdate);
				System.out.println("Connect to a network and try again to get latest market updates.");
			}else{
				System.out.println("No markets are loaded.");
				System.out.println("Connect to a network and try again to populate markets");
			}
			
		}
		
	}
	
	private long lastUpdate(){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		try{	        
	        String compiledQuery = "SELECT MAX (Date) AS Date FROM markets;"; 
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        long lastUpdateDate;
	        long start = System.currentTimeMillis();
	        ResultSet resultSet = preparedStatement.executeQuery();
	        long end = System.currentTimeMillis();
	        System.out.println("total time taken to find latest date = " + (end - start) + " ms");
	        
	        resultSet.getLong("Date");
	        if(resultSet.wasNull()){
	        	lastUpdateDate = 0;
	        }else{
	        	lastUpdateDate = resultSet.getLong("Date");
	        }	        
	        preparedStatement.close();
        	connection.close();
        	
        	return lastUpdateDate;
        }catch(SQLException ex){
			System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
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
