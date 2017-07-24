package market;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import utils.UpdateUtils;

public class UpdateMarketTable {

	long lastUpdateDate;
	
	public UpdateMarketTable(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
		if(DbLoader.isConnected()){
			System.out.println("Updating last record using network connection...");
			updateLastRecord();
		}else{
			System.out.println("Updating last record using offline...");
			updateLastRecordOffline();
		}
		System.out.println("Last record updated...");
	}
	
	private void updateLastRecordOffline() {
		File folder = new File("com/speculation1000/dbloader/src/main/resources/offline/");
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
				for(int i = priceDataFromFile.size() - 1; i > 0; i--){
					priceData = new PriceData(currencyPair.toString(),priceDataFromFile.get(i).split(","));
					if(priceData.getDate().getTime() == lastUpdateDate){
						priceList.add(priceData);
					}else if(priceData.getDate().getTime() < lastUpdateDate){
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
		
		new UpdateUtils().updateLatestRecord(priceList, lastUpdateDate);
	}

	public void updateLastRecord(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		List<CurrencyPair> currencyPairList = exchange.getExchangeSymbols();
		PriceData priceData;
		List<PriceData> priceList = new ArrayList<>();
		List<PoloniexChartData> priceListRaw;
		
		for(CurrencyPair currencyPair : currencyPairList){
			try {
				priceListRaw = Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(currencyPair, lastUpdateDate / 1000,
								lastUpdateDate / 1000, PoloniexChartDataPeriodType.PERIOD_86400));
					
				for(PoloniexChartData dayData : priceListRaw){
						priceData = new PriceData(currencyPair.toString(),dayData.getDate(),dayData.getHigh(),dayData.getLow(),dayData.getOpen(),dayData.getClose(),dayData.getVolume());
						priceList.add(priceData);
				}
				System.out.println(currencyPair.toString());
			}catch (IOException e) {
				throw new ExchangeException(e.getMessage());
			}
		}
		
		new UpdateUtils().updateLatestRecord(priceList, lastUpdateDate);
	}
		
  }

