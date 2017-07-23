package price;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceData {
	
	private String marketName;
	
	private Date date;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private BigDecimal open;
	
	private BigDecimal close;
	
	private BigDecimal volume;
	
	private BigDecimal trueRange;
	
	private int highLow;
	
	private boolean isEntry;
	
	public PriceData(String marketName,Date date, BigDecimal high, BigDecimal low, BigDecimal open, BigDecimal close, BigDecimal volume){
		this.marketName = marketName.replace("/", "") + ":POLO";
		this.date = date;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.volume = volume;
	}
	
	public String getMarketName(){
		return marketName;
	}
	
	public PriceData(String[] singleDayPriceDataStrArr) {
		try{
			//only need if date is weird in file...
			DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");			
			setDate(format.parse(singleDayPriceDataStrArr[0].trim()));
		}catch(ParseException e){
			System.out.println("Error parsing date in price data array");
		}
		
		high = new BigDecimal(singleDayPriceDataStrArr[1]);
		low = new BigDecimal(singleDayPriceDataStrArr[2]);
		open =	new BigDecimal(singleDayPriceDataStrArr[3]);
		close = new BigDecimal(singleDayPriceDataStrArr[4]); 
		volume = new BigDecimal(singleDayPriceDataStrArr[5]);		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getTrueRange() {
		return trueRange;
	}
	
	public void setTrueRange(BigDecimal trueRange){
		this.trueRange = trueRange;
	}
	
	public int getHighLow() {
		return highLow;
	}

	public void setHighLow(int highLow) {
		this.highLow = highLow;
	}

	public void setEntry(boolean isEntry) {
		this.isEntry = isEntry;
	}

	public boolean isEntry() {
		return isEntry;
	}
	
	@Override
	public String toString(){
		return this.getDate() + "," + this.getHigh() + "," + this.getLow() + "," + this.getOpen() + "," + this.getClose() + 
				"," + this.getVolume();
	}
}
