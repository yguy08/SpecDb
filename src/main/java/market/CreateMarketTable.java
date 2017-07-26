package market;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import loader.Connect;

public class CreateMarketTable {
	
	public void insertIfNotExists() {        
        String sql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Exchange character NOT NULL,\n"
                + "	Date int NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int\n"
                + ");";
 
        try{
        	Connection conn = new Connect().getConnection();
        	PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("Table created...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public static void main(String[] args){
		new CreateMarketTable().insertIfNotExists();
	}

}
