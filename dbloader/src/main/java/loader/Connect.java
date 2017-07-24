package loader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

	public Connect() {
		
	}
	
    public Connection getConnection() {
        // SQLite connection string
        String url = "jdbc:sqlite:com/speculation1000/dbloader/src/main/resources/db/Speculation1000.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
