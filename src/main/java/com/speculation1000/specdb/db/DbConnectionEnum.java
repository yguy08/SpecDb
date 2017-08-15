package com.speculation1000.specdb.db;

public enum DbConnectionEnum {
	
	H2_MAIN("jdbc:h2:","tcp://localhost:8082/" + System.getProperty("user.home") + "/SpecDb/db/Speculation1000-H2","org.h2.Driver"),
	H2_TEST("jdbc:h2:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000-H2Test","org.h2.Driver"),
	SQLITE_MAIN("jdbc:sqlite:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000.db","org.sqlite.JDBC"),
	SQLITE_TEST("jdbc:sqlite:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000-tmp.db","org.sqlite.JDBC");
		
	private String driver;
	private String url;
	private String className;
	
	DbConnectionEnum(String driver, String url, String className){
		this.driver = driver;
		this.url = url;
		this.className = className;
	}
	
	public String getConnectionString(){
		return driver + url;
	}
	
	public String getClassForName(){
		return className;
	}
	
	//h2 connection string help
	//https://stackoverflow.com/questions/35854425/java-application-with-h2-database

}
