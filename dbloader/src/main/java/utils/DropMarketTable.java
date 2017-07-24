package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import loader.Connect;

public class DropMarketTable {
	
	public void dropTable(){
		String sql = "DROP TABLE markets";
		Connection connection = new Connect().getConnection();
        
		try{
        	PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("TABLE dropped...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

	}
    
	public static void main(String[] args){
    	new DropMarketTable().dropTable();
    }

}
