package com.speculation1000.specdb.marketdata;

import java.io.IOException;
import java.time.Instant;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.poloniex.PoloniexExchange;

public class PoloniexTickProducer implements ITickProducer {
	
	private static final Exchange POLONIEX = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
	
	private final ITickQueue queue;
	
	public PoloniexTickProducer(ITickQueue queue) {
		this.queue = queue;
	}

	@Override
	public void produce() throws InterruptedException {
		try {
			Ticker ticker = POLONIEX.getMarketDataService().getTicker(new CurrencyPair("BTC","USDT"));
			queue.put(new PoloniexTick(ticker.getCurrencyPair().toString(),Instant.now().getEpochSecond(),
					ticker.getLast().doubleValue()));
		} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | ExchangeException
				| IOException e) {
			e.printStackTrace();
		}
        Thread.sleep(5000);
	}

}
