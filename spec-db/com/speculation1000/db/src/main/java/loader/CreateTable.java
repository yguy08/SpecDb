package loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateTable {

	public CreateTable() {
		
	}
	
	public void insertIfNotExists() {        
        String sql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Date datetime NOT NULL,\n"
                + " Open decimal,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
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

}
