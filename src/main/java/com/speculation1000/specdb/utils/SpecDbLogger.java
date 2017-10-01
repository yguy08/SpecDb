package com.speculation1000.specdb.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SpecDbLogger {
	
	private static final String SPECDB_APP_LOGGER_NAME = "SpsApplicationLog.standard";
	
	private static final SpecDbLogger appLog = new SpecDbLogger(SPECDB_APP_LOGGER_NAME);
	
	protected Logger logger;
    
	static private FileHandler fileTxt;
    
    private SpecDbLogger(String name){
    	String path = System.getProperty("user.home") + "/SpecDb/logs";
    	try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	try {
			fileTxt = new FileHandler("%h/SpecDb/logs/SpecDb%u.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
    	logger = Logger.getLogger(name);
    	logger.setLevel(Level.INFO);
        logger.addHandler(fileTxt);
        fileTxt.setFormatter(new SimpleFormatter());
    }
    
    public static SpecDbLogger getSpecDbLogger(){
    	return appLog;
    }
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg){
    	logger.logp(level, sourceClass, sourceMethod, msg);
    }

}
