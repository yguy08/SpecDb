package utils.log;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SpecDbLogger {
	
	private static final String SPECDB_APP_LOGGER_NAME = "SpsApplicationLog.standard";
	private static final SpecDbLogger appLog = new SpecDbLogger(SPECDB_APP_LOGGER_NAME);
	protected Logger logger;
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    
    private SpecDbLogger(String name){
    	
    	try {
			fileTxt = new FileHandler("SpecDb.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
    	logger = Logger.getLogger(name);
    	logger.setLevel(Level.INFO);
        logger.addHandler(fileTxt);
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
    }
    
    public static SpecDbLogger getSpecDbLogger(){
    	return appLog;
    }
    
    public void log(String fqcl, String message){
    	logger.info(fqcl + ":\n" +"* " + message);
    }

	public static void main(String[] args) {
		SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
		specLogger.log(SpecDbLogger.class.getName(),"Hello world");
	}

}
