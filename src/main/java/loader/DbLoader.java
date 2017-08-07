package loader;

import java.time.Instant;
import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class DbLoader {
		
	private static boolean netConnected;
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static final Instant START_UP_TS = Instant.now();
	
	private static Instant currentUpdateTS = null;
					
	public static void main(String[] args) {
		specLogger.log(DbLoader.class.getName(), DbLoader.startUpStatusMessage());	
		if(args.length > 0){
			String command = args[0];
			switch(command){
			case "quick":
			case "q":
				new QuickMode().startApp();;
				break;
			case "normal":
			case "n":
				new NormalMode().startApp();
				break;
			case "r":
				new PoloRestore().startApp();
				break;				
			default:
				new NormalMode().startApp();
				break;
			}
			
		}else{
			new NormalMode().startApp();
		}
	}
	
	//Return the current update TS
	public static Instant getCurrentUpdateTS(){
		if(currentUpdateTS == null){
			return Instant.now();
		}else{
			return currentUpdateTS;
		}
	}
	
	public static boolean isConnected() {
		return netConnected;
	}

	public static Instant getStartUpTs() {
		return START_UP_TS;
	}
	
	/*
	 * @returns int hours of system uptime
	 */
	public static long getSystemUptime(){
		return DbLoader.getCurrentUpdateTS().getEpochSecond() - DbLoader.getStartUpTs().getEpochSecond();
	}
	
	public static String startUpStatusMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********** SpecDb ************** \n");
		sb.append("[ Digital Asset Price Database ]\n");
		sb.append("********************************\n");
		sb.append("* Start Up: \n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartUpTs()) + "\n");
		sb.append("********************************\n");
		sb.append("          [ INFO ]\n");
		sb.append("* Updates every 15 mins\n");
		sb.append("* New day starts at Zulu Midnight\n");
		sb.append("* Supported Exchanges: \n");
		sb.append("\t" + "- Poloniex\n");
		sb.append("\t" + "- Bittrex\n");
		sb.append("********************************\n");
		return sb.toString();
	}
}
