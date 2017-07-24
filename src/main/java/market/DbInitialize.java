package market;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import loader.DbLoader;
import price.PriceData;
import utils.InsertUtils;

public class DbInitialize {
	
	public DbInitialize() {
		if(DbLoader.isConnected()){
			System.out.println("Updating entire db using network connection...");
			batchInsertMarkets();
		}else{
			System.out.println("Updating entire db using offline...");
			batchInsertMarketsOffline();
		}
		System.out.println("Db init complete...");
	}
	
	public void batchInsertMarkets(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		PriceData priceData;
		List<PriceData> priceList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw;
		long date = new Date().getTime() / 1000;
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
							.getPoloniexChartData(currencyPair, date - 365 * 10 * 24 * 60 * 60,
								date, PoloniexChartDataPeriodType.PERIOD_86400));
					for(PoloniexChartData dayData : priceListRaw){
						priceData = new PriceData(currencyPair.toString(),dayData.getDate(),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume());
						priceList.add(priceData);
					}
					System.out.println("Loaded " + currencyPair.toString());
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
		}
		
		new InsertUtils().insertNewRecords(priceList);
	}

	public void batchInsertMarketsOffline() {
		File folder = new File("src/main/resources/offline/");
		File[] listOfFiles = folder.listFiles();
		
		List<String> currencyPairList = new ArrayList<>();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	currencyPairList.add(listOfFiles[i].getName());
	      }
	    }
	    
	    List<String> priceDataFromFile;
	    PriceData priceData;
	    List<PriceData> priceList = new ArrayList<>();
		for(String currencyPair : currencyPairList){			
			try {
				priceDataFromFile = Files.readAllLines(Paths.get("com/speculation1000/dbloader/src/main/resources/offline/" + currencyPair));
				for(String priceDataStr : priceDataFromFile){
					priceData = new PriceData(currencyPair.toString(),priceDataStr.split(","));
					priceList.add(priceData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
		new InsertUtils().insertNewRecords(priceList);		
	}
}
