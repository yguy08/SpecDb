package com.speculation1000.specdb.db;

public enum DbConnectionEnum {
	
	H2_MAIN("jdbc:h2:","Speculation1000h2.db"),
	H2_TEST("jdbc:h2:","Speculation1000h2-t.db"),
	SQLITE_MAIN("jdbc:sqlite:","Speculation1000sqlite.db"),
	SQLITE_TEST("jdbc:sqlite:","Speculation1000sqlite-t.db");
	
	private static final String USER_HOME = System.getProperty("user.home") + "/SpecDb/db/";
	
	private String dbDriverName;
	private String dbName;	
	
	DbConnectionEnum(String dbDriverName, String dbName){
		this.dbDriverName = dbDriverName;
		this.dbName = dbName;
	}
	
	public String getConnectionString(){
		return dbDriverName + USER_HOME + dbName;
	}

}
