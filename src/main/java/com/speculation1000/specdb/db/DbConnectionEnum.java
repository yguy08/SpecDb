package com.speculation1000.specdb.db;

public enum DbConnectionEnum {
	
	H2_MAIN("jdbc:h2:","tcp://localhost:8089/" + System.getProperty("user.home") + "/SpecDb/db/Speculation1000-H2","org.h2.Driver"),
	H2_CONNECT("jdbc:h2:","tcp://192.168.1.151:8082/" + "/home/pi/SpecDb/db/Speculation1000-H2","org.h2.Driver"),
	H2_TEST("jdbc:h2:",System.getProperty("user.home") + "/SpecDb/db/Speculation1000-H2","org.h2.Driver");
		
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

}
