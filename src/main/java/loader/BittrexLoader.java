package loader;

import java.io.IOException;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexTicker;
import org.knowm.xchange.bittrex.v1.service.BittrexMarketDataService;

public class BittrexLoader {

	public BittrexLoader() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
		BittrexMarketDataService bmds = (BittrexMarketDataService) exchange.getMarketDataService();
		List<BittrexTicker> tickerList = bmds.getBittrexTickers();
		for(BittrexTicker bt : tickerList){
			System.out.println(bt.toString());
		}
		
	}

}
