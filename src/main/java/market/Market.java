package market;

import java.math.BigDecimal;

public class Market {
	
	private String symbol;
		
	private String exchange;
	
	private long date;
	
	private BigDecimal open;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private int volume;
	
	public Market(String symbol,String exchange,long date,
			BigDecimal high,BigDecimal low,BigDecimal open,BigDecimal close,int volume){
		this.symbol = symbol;
		this.exchange = exchange;
		this.date = date;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.volume = volume;
	}
	
	public String getSymbol(){
		return symbol;
	}

	public String getExchange() {
		return exchange;
	}

	public long getDate() {
		return date;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public BigDecimal getClose() {
		return close;
	}

	public int getVolume() {
		return volume;
	}
	
	@Override
	public String toString(){
		return symbol + ":" + exchange;
	}
	
}
