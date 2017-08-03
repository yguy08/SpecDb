package loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexTicker;
import org.knowm.xchange.bittrex.v1.service.BittrexMarketDataService;

import dao.MarketDAO;
import db.DbManager;
import utils.SpecDbDate;
import utils.log.SpecDbLogger;

public class BittrexLoader {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	public BittrexLoader() throws IOException {
		List<BittrexTicker> tickerList = new ArrayList<>();
		List<MarketDAO> marketList = new ArrayList<>();

		try{
			Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
			BittrexMarketDataService bmds = (BittrexMarketDataService) exchange.getMarketDataService();
			tickerList = bmds.getBittrexTickers();
		}catch(Exception e){
			System.out.println("Failed loading bittrex market!");
			System.out.println(e.getMessage());
		}
		
		try{
			for(BittrexTicker bt : tickerList){
				MarketDAO market = new MarketDAO();
				market.setSymbol(bt.getMarketName());
				market.setExchange("TREX");
				market.setDate(SpecDbDate.getTodayUtcEpochSeconds());
				market.setHigh(bt.getHigh());
				market.setLow(bt.getLow());
				market.setOpen(bt.getPrevDay());
				market.setClose(bt.getLast());
				market.setVolume(bt.getBaseVolume().intValue());
				marketList.add(market);
				System.out.println(bt.toString());
			}
		}catch(Exception e){
			System.out.println("Failed adding bittrex ticker to list!");
		}
		
		if(SpecDbDate.isNewDay()){
			String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
			new DbManager().insertBatchMarkets(marketList, insertQuery);			
		}else{
    		String deleteSql = "DELETE from markets WHERE Date = (SELECT Max(Date) from markets where Exchange = 'TREX') AND Exchange = 'TREX'";
    		int recordsDeleted = new DbManager().deleteRecords(deleteSql);
    		specLogger.log(DbLoader.class.getName(), "Same Day..Records deleted: " + recordsDeleted);
			String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
			new DbManager().insertBatchMarkets(marketList, insertQuery);			
		}
	}

	public static void main(String[] args) throws IOException {
		
	}

}
