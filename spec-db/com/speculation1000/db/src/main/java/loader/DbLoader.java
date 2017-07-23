package loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbLoader {
		
	public DbLoader() {
		
		//create table if does not exist
		new CreateTable().insertIfNotExists();
		
		//get last update date
		long lastUpdateDate = getLastDate();
        System.out.println("Last update " + lastUpdateDate);
		
		if(lastUpdateDate > 0){
	        System.out.println("Db populated. Updating last recorded entry @ " + lastUpdateDate);
			new UpdateDb(lastUpdateDate).updateLastRecord();
	        System.out.println("Last recorded entry updated...");
	        System.out.println("Fetching new data...");
			new FetchNewDb(lastUpdateDate).fetchNewRecords();
	        System.out.println("Done...");
		}else{
	        System.out.println("Db full init...");
			new DbInit().batchInsertMarkets();
		}
	}

	public static void main(String[] args) {
		//create db
		new DbLoader();
	}
	
	private long getLastDate(){
		PreparedStatement preparedStatement;
		try{
			Connection connection = new Connect().getConnection();	        
	        String compiledQuery = "SELECT MAX (Date) AS Date FROM markets"; 
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        long lastUpdateDate;
	        long start = System.currentTimeMillis();
	        ResultSet resultSet = preparedStatement.executeQuery();
	        long end = System.currentTimeMillis();
	        System.out.println("total time taken to find latest date = " + (end - start) + " ms");
	        
	        resultSet.getDate("Date");
	        if(resultSet.wasNull()){
	        	lastUpdateDate = 0;
	        }else{
	        	lastUpdateDate = resultSet.getDate("Date").getTime();
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
}
