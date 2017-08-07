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

public class PoloniexLoader {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void poloUpdater(){
		
		Map<String,List<PoloniexChartData>> poloniexChartData = 
				PoloUtils.getPoloniexChartData(SpecDbDate.getTodayUtcEpochSeconds(), 9999999999L);
		
		List<MarketDAO> marketList = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for(Map.Entry<String, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
			if(i < 10){
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
				market.setDate(SpecDbDate.getTodayUtcEpochSeconds());
				market.setHigh(dayData.getHigh());
				market.setLow(dayData.getLow());
				market.setOpen(dayData.getOpen());
				market.setClose(dayData.getClose());
				market.setVolume(dayData.getVolume().intValue());
				marketList.add(market);
			}
		}
		try{
			specLogger.log(PoloniexLoader.class.getName(), sb.toString());
			if(SpecDbDate.isNewDay()){
				String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
				new DbManager().insertBatchMarkets(marketList, insertQuery);
			}else{
	    		String deleteSql = "DELETE from markets WHERE Date = (SELECT Max(Date) from markets where Exchange = 'POLO') AND Exchange = 'POLO'";
	    		int recordsDeleted = new DbManager().deleteRecords(deleteSql);
	    		specLogger.log(DbLoader.class.getName(), "Same Day..Records deleted: " + recordsDeleted);
				String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
				new DbManager().insertBatchMarkets(marketList, insertQuery);
			}
		}catch(Exception e){
			StringBuffer sbuff = new StringBuffer();
			for(StackTraceElement ste : e.getStackTrace()){
				sbuff.append(ste.toString() + "\n");
			}
			specLogger.log(PoloniexLoader.class.getName(), sbuff.toString());
		}

	}
	
	public PoloniexLoader(){
		String lastUpdateSql = "SELECT MAX (Date) AS Date FROM markets WHERE exchange = "+"'POLO'";
		List<MarketDAO> marketList = new DbManager().genericMarketQuery(lastUpdateSql);
		long lastUpdateDate = marketList.get(0).getDate();
		System.out.println("Db populated up to " + lastUpdateDate);
		specLogger.log(DbLoader.class.getName(), "Last POLO update: " + lastUpdateDate);
		
    	long daysMissing = ((Instant.now().getEpochSecond() - lastUpdateDate) / 86400);
    	if(daysMissing <= 1){
    		//delete last record before updating all
    		String deleteSql = "DELETE from markets WHERE Date = " + lastUpdateDate;
    		int recordsDeleted = new DbManager().deleteRecords(deleteSql);
    		specLogger.log(DbLoader.class.getName(), "Records deleted: " + recordsDeleted);
    	}
		
		//fetch!
		specLogger.log(DbLoader.class.getName(), "Need next " + daysMissing + " day data");
		fetchNewPoloRecords(lastUpdateDate);
		specLogger.log(DbLoader.class.getName(), "Done loading Polo markets");
	}
	
	private void fetchNewPoloRecords(long lastUpdateDate){
		Map<String,List<PoloniexChartData>> poloniexChartData = 
				PoloUtils.getPoloniexChartData(lastUpdateDate, 9999999999L);
		
		List<MarketDAO> marketList = new ArrayList<>();
		for(Map.Entry<String, List<PoloniexChartData>> e : poloniexChartData.entrySet()){
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
		String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
		new DbManager().insertBatchMarkets(marketList, insertQuery);
		specLogger.log(DbLoader.class.getName(), "Done inserting POLO markets into DB");
	}
}
