package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import loader.Connect;

public class SelectUtils {
	
	public long selectLastUpdate(){
		PreparedStatement preparedStatement;
		Connection connection = new Connect().getConnection();
		try{	        
	        String compiledQuery = "SELECT MAX (Date) AS Date FROM markets"; 
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

}
