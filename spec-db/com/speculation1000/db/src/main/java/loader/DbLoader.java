package loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbLoader {
		
	public DbLoader() {
		
		//create table if does not exist
		new CreateTable().insertIfNotExists();
		
		long lastUpdateDate = getLastDate();
		
		if(lastUpdateDate > 0){
			new UpdateDb(lastUpdateDate).updateLastRecord();
			new FetchNewDb(lastUpdateDate).fetchNewRecords();
		}else{
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
