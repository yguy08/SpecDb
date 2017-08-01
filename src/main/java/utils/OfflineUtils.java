package utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dao.MarketDAO;
import db.DbManager;

public class OfflineUtils {
	
	public void batchInsertMarketsOffline() {
		List<String> marketListFromFile = new ArrayList<>();
	    List<MarketDAO> marketList = new ArrayList<>();		
		
	    try {
			marketListFromFile = Files.readAllLines(Paths.get("marketlist.csv"));
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
	    
	    for(String marketStr : marketListFromFile){
			String marketArr[] = marketStr.split(",");
			MarketDAO market = new MarketDAO();
			market.setSymbol(marketArr[0].trim());
			market.setExchange(marketArr[1].trim());
			market.setDate(Long.parseLong(marketArr[2]));
			market.setHigh(new BigDecimal(marketArr[3].trim()));
			market.setLow(new BigDecimal(marketArr[4].trim()));
			market.setOpen(new BigDecimal(marketArr[5].trim()));
			market.setClose(new BigDecimal(marketArr[6].trim()));
			market.setVolume(Integer.parseInt(marketArr[7].trim()));
			marketList.add(market);
		}
		
	    String sqlCommand = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
	    new DbManager().insertBatchMarkets(marketList, sqlCommand);
	}
	
	public static void main(String[] args){
		new OfflineUtils().batchInsertMarketsOffline();
		System.out.println("Done!");
	}
}
