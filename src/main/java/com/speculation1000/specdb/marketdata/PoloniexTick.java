package com.speculation1000.specdb.marketdata;

public class PoloniexTick implements ITick {
	
    private String tickerSymbol;
    
    private long timestamp;
    
    private double last;    
  
    public PoloniexTick(String tickerSymbol, long timestamp, double last) {
        this.tickerSymbol = tickerSymbol;
        this.timestamp = timestamp;
        this.last = last;
    }
    
    @Override 
    public String toString() {
    	return this.tickerSymbol + " " + this.timestamp + " " + this.last;
    }
}
