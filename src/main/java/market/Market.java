package market;

import java.math.BigDecimal;

public class Market {
	
	private String base;
	
	private String counter;
	
	private String exchange;
	
	private long date;
	
	private BigDecimal open;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private int volume;
	
	public Market(String base,String counter,String exchange,long date,
			BigDecimal high,BigDecimal low,BigDecimal open,BigDecimal close,int volume){
		this.base = base;
		this.counter = counter;
		this.exchange = exchange;
		this.date = date;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.volume = volume;
	}
	
	public String getBase() {
		return base;
	}

	public String getCounter() {
		return counter;
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
		return base + counter + ":" + exchange;
	}
	
}
