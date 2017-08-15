package com.speculation1000.specdb.db;

public enum DbConnectionEnum {
	
	H2_MAIN("jdbc:h2:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000-H2.db"),
	H2_TEST("jdbc:h2:",""),
	SQLITE_MAIN("jdbc:sqlite:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000.db"),
	SQLITE_TEST("jdbc:sqlite:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000.db");
		
	private String dbDriverName;
	private String dbPath;	
	
	DbConnectionEnum(String dbDriverName, String dbPath){
		this.dbDriverName = dbDriverName;
		this.dbPath = dbPath;
	}
	
	public String getConnectionString(){
		return dbDriverName + dbPath;
	}

}
