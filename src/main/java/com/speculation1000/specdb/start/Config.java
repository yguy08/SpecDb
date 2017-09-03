package com.speculation1000.specdb.start;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	
	private String DATABASE;
	
	private String KEY;
	
	private String SECRET;
	
	public Config(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);
			
			DATABASE = prop.getProperty("database");
			KEY = prop.getProperty("api-key");
			SECRET = prop.getProperty("api-secret");

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
		return DATABASE;
	}
	
	public String getKey() {
		return KEY;
	}
	
	public String getSecret() {
		return SECRET;
	}

}
