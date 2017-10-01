package com.speculation1000.specdb.start;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	
	private static String database;
	
	private static String polokey;
	
	private static String polosecret;
	
	private static String trexkey;
	
	private static String trexsecret;
	
	private static String initialdelay;
	
	private static String runperiod;
	
	private static String entryFlag;
	
	private static String exitFlag;
	
	private static String volLimit;
	
	public static void configSetUp() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");
			// load a properties file
			prop.load(input);			
			database = prop.getProperty("database");
			polokey = prop.getProperty("polo-api-key");
			polosecret = prop.getProperty("polo-api-secret");
			initialdelay = prop.getProperty("initial-delay");
			runperiod = prop.getProperty("run-period");
			entryFlag = prop.getProperty("entry-flag");
			exitFlag = prop.getProperty("exit-flag");
			trexkey = prop.getProperty("trex-api-key");
			trexsecret = prop.getProperty("trex-api-secret");
			volLimit = prop.getProperty("vol-limit");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getDatabase(){
		return database;
	}
	
	public static String getPoloKey() {
		return polokey;
	}
	
	public static String getPoloSecret() {
		return polosecret;
	}
	
	public static String getTrexKey() {
		return trexkey;
	}
	
	public static String getTrexSecret() {
		return trexsecret;
	}
	
	public static int getInitialDelay(){
		return Integer.parseInt(initialdelay);
	}
	
	public static int getRunPeriod(){
		return Integer.parseInt(runperiod);
	}
	
	public static int getEntryFlag(){
		return Integer.parseInt(entryFlag);
	}
	
	public static int getExitFlag(){
		return Integer.parseInt(exitFlag);
	}
	
	public static int getVolLimit(){
		return Integer.parseInt(volLimit);
	}

}
