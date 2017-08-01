package utils;

import db.DbManager;

public class DropTable {
    
	public static void main(String[] args){
		String sql = "DROP TABLE markets";
    	new DbManager().dropTable(sql);
    }

}
