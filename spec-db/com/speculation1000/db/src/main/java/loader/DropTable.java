package loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DropTable {

	public DropTable() {

	}
	
	public void dropTable(){
		String sql = "DROP TABLE markets";
		
        try{
        	Connection conn = new Connect().getConnection();
        	PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("TABLE dropped...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

	}
    
	public static void main(String[] args){
    	new DropTable().dropTable();
    }

}
