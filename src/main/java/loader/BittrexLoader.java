package loader;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexTicker;
import org.knowm.xchange.bittrex.v1.service.BittrexMarketDataService;

import dao.MarketDAO;
import db.DbManager;

public class BittrexLoader {

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
				ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Etc/UTC"));
				market.setDate(zdt.toEpochSecond());
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
		
		try{
			String insertQuery = "INSERT INTO markets(Symbol,Exchange,Date,High,Low,Open,Close,Volume) VALUES(?,?,?,?,?,?,?,?)";
			new DbManager().insertBatchMarkets(marketList, insertQuery);
		}catch(Exception e){
			System.out.println("Failed adding bittrex ticker to db!");
		}
		
	}

	public static void main(String[] args) throws IOException {
		
	}

}
