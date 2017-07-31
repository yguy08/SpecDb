package loader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	
    public Connection getConnection() {
        // SQLite connection string
        String url = "jdbc:sqlite:Speculation1000.db";
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return conn;
    }
    
}
