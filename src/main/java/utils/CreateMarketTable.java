package utils;

import db.DbManager;

public class CreateMarketTable {
	
	public static void main(String[] args){
        String sql = "CREATE TABLE IF NOT EXISTS markets (\n"
                + "	Symbol character NOT NULL,\n"
                + "	Exchange character NOT NULL,\n"
                + "	Date int NOT NULL,\n"
                + " High decimal,\n"
                + " Low decimal,\n"
                + " Open decimal,\n"
                + " Close decimal,\n"
                + " Volume int,\n"
                + " ATR decimal\n"
                + ");";
        
    	new DbManager().createTable(sql);
	}

}
