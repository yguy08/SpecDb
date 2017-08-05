package utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

public class PoloUtils {
	
	public static Map<String,List<PoloniexChartData>> getPoloniexChartData(long start, long end){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		Map<String,List<PoloniexChartData>> poloChartDataMap = new HashMap<>();
		
		for(CurrencyPair pair : exchange.getExchangeSymbols()){
			try {
				List<PoloniexChartData> poloniexChartData = (Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(pair, SpecDbDate.getTodayUtcEpochSeconds(),
						9999999999L, PoloniexChartDataPeriodType.PERIOD_86400)));
				poloChartDataMap.put(pair.base.toString() + pair.counter.toString(), poloniexChartData);
				//logger
			} catch (IOException e) {
				e.printStackTrace();
				//logger
			}
		}
		return poloChartDataMap; 
	}
}
