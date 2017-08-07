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

import loader.PoloniexLoader;
import utils.log.SpecDbLogger;

public class PoloUtils {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static Map<String,List<PoloniexChartData>> getPoloniexChartData(long start, long end){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		Map<String,List<PoloniexChartData>> poloChartDataMap = new HashMap<>();
		List<CurrencyPair> pairList = exchange.getExchangeSymbols();
		specLogger.log(PoloUtils.class.getName(),  "Loading Polo markets...");
		for(CurrencyPair pair : pairList){
			try {
				List<PoloniexChartData> poloniexChartData = (Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(pair, SpecDbDate.getTodayUtcEpochSeconds(),
						9999999999L, PoloniexChartDataPeriodType.PERIOD_86400)));
				poloChartDataMap.put(pair.base.toString() + pair.counter.toString(), poloniexChartData);
			} catch (IOException e) {
				StringBuffer sb = new StringBuffer();
				for(StackTraceElement ste : e.getStackTrace()){
					sb.append(" [" + ste.toString() + "],");
				}
				specLogger.log(PoloniexLoader.class.getName(), sb.toString());
			}
		}
		return poloChartDataMap; 
	}
	
	public static Map<String,List<PoloniexChartData>> getEntirePoloOHLC(){
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
		Map<String,List<PoloniexChartData>> poloChartDataMap = new HashMap<>();
		List<CurrencyPair> pairList = exchange.getExchangeSymbols();
		specLogger.log(PoloUtils.class.getName(),  "Loading Entire Polo history...");
		for(CurrencyPair pair : pairList){
			try {
				List<PoloniexChartData> poloniexChartData = (Arrays.asList(((PoloniexMarketDataServiceRaw) exchange.getMarketDataService())
						.getPoloniexChartData(pair, SpecDbDate.getTodayUtcEpochSeconds() - (86400 * 10 * 365),
						9999999999L, PoloniexChartDataPeriodType.PERIOD_86400)));
				poloChartDataMap.put(pair.base.toString() + pair.counter.toString(), poloniexChartData);
			} catch (IOException e) {
				StringBuffer sb = new StringBuffer();
				for(StackTraceElement ste : e.getStackTrace()){
					sb.append(" [" + ste.toString() + "]");
				}
				specLogger.log(PoloniexLoader.class.getName(), sb.toString());
			}
		}
		return poloChartDataMap; 
	}
}
