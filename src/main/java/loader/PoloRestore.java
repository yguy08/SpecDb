package loader;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;

import dao.MarketDAO;
import db.DbManager;
import utils.PoloUtils;
import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class PoloRestore implements Mode {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
		
	private static Instant startRunTS = null;

	@Override
	public void setStartRunTS(Instant instant) {
		startRunTS = instant;
	}

	@Override
	public Instant getStartRunTS() {
		return startRunTS;
	}

	@Override
	public String getStartRunMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ POLO - RESTORE ]\n");
		sb.append("********************************\n");
		sb.append("          [ START ]\n");
		sb.append("* At: ");
		sb.append(SpecDbDate.instantToLogStringFormat(getStartRunTS()) + "\n");
		sb.append("********************************\n");
		return sb.toString();
	}

	@Override
	public String getEndRunMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("********************************\n");
		sb.append("          [ POLO - RESTORE ]\n");
		Instant end = Instant.now();
		sb.append("********************************\n");
		sb.append("          [ RESULTS ]\n");
		sb.append("* Time: ");
		sb.append(end.getEpochSecond() - getStartRunTS().getEpochSecond() + " sec\n");
		sb.append("********************************\n");
		int i = 0;
		for(MarketDAO market : new DbManager().getPoloRestoreResults()){
			if(i < 10){
				sb.append("* " + market.toString() + "],");
				i++;
			}else{
				sb.append("* " + market.toString() + "]\n");
				i=0;
			}
		}
		sb.append("********************************\n");
		sb.append("* Restore Complete...\n");
		sb.append("********************************\n");
		return sb.toString();
	}

	@Override
	public void startApp() {
		new PoloRestore();
	}

	public  PoloRestore() {
		new DbManager().createTable();
		setStartRunTS(Instant.now());
		
		specLogger.log(PoloRestore.class.getName(),getStartRunMessage());
		
		int removed = new DbManager().deleteAllPoloRecords();
		
		specLogger.log(PoloRestore.class.getName(), "Records removed: " + removed);
		
		Map<String,List<PoloniexChartData>> poloniexChartData = PoloUtils.getEntirePoloOHLC();
		
		List<MarketDAO> marketList = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for(Map.Entry<String, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
			if(i < 20){
				sb.append("["+e.getKey()+"],");
				i++;
			}else{
				sb.append("["+e.getKey()+"]\n");
				i = 0;
			}
			for(PoloniexChartData dayData : e.getValue()){
				MarketDAO market = new MarketDAO();
				market.setSymbol(e.getKey());
				market.setExchange("POLO");
				market.setDate(SpecDbDate.dateToUtcMidnightSeconds(dayData.getDate()));
				market.setHigh(dayData.getHigh());
				market.setLow(dayData.getLow());
				market.setOpen(dayData.getOpen());
				market.setClose(dayData.getClose());
				market.setVolume(dayData.getVolume().intValue());
				marketList.add(market);
			}
		}
		
		
	try{
		specLogger.log(PoloRestore.class.getName(), "Inserting the following: " + sb.toString());
		String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
		new DbManager().insertBatchMarkets(marketList, insertQuery);		
		specLogger.log(PoloRestore.class.getName(),getEndRunMessage());
		}catch(Exception e){
			StringBuffer sbuff = new StringBuffer();
			for(StackTraceElement ste : e.getStackTrace()){
				sbuff.append(ste.toString() + "\n");
			}
			specLogger.log(PoloRestore.class.getName(), sbuff.toString());
		}
	}

}
