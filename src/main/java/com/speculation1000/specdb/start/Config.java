package com.speculation1000.specdb.start;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	
	private String database;
	
	private String polokey;
	
	private String polosecret;
	
	private String trexkey;
	
	private String trexsecret;
	
	private String initialdelay;
	
	private String runperiod;
	
	private String entryFlag;
	
	private String exitFlag;
	
	public Config(){
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
	
	public String getDatabase(){
		return database;
	}
	
	public String getPoloKey() {
		return polokey;
	}
	
	public String getPoloSecret() {
		return polosecret;
	}
	
	public String getTrexKey() {
		return trexkey;
	}
	
	public String getTrexSecret() {
		return trexsecret;
	}
	
	public boolean getInitialDelay(){
		if(initialdelay.equalsIgnoreCase("true")){
			return true;
		}else{
			return false;
		}
	}
	
	public int getRunPeriod(){
		return Integer.parseInt(runperiod);
	}
	
	public int getEntryFlag(){
		return Integer.parseInt(entryFlag);
	}
	
	public int getExitFlag(){
		return Integer.parseInt(exitFlag);
	}

}
