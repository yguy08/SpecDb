package com.speculation1000.specdb.market;

public class Symbol implements Comparable<Symbol> {
	
	private final String symbol;
	
	private String base;
	
	private String counter;
	
	private String exchange;
	
	private final String UNKNOWN = "UNKNOWN";
	
	public Symbol(Market market){
		symbol = market.getSymbol().toString();
	}
	
	public Symbol(String base, String counter, String exchange){
		this.base = base;
		this.counter = counter;
		this.exchange = exchange;
		symbol = base+"/"+counter+":"+exchange;
	}
	
	public Symbol(String symbol){
		if(symbol!=null && !symbol.isEmpty()){
			int base = symbol.indexOf("/");
			int counter = symbol.indexOf(":");
			if(base != -1 && counter != -1){
				this.base = symbol.substring(0,base);
				this.counter = symbol.substring(base,counter);
				this.exchange = symbol.substring(counter);
				this.symbol = symbol;
			}else{
				this.base = UNKNOWN;
				this.counter = UNKNOWN;
				this.exchange = UNKNOWN;
				this.symbol = UNKNOWN;
			}
		}else{
			this.symbol = UNKNOWN;
		}
	}
	
	public String getBase(){
		return base;
	}
	
	public void setBase(String base){
		this.base = base;
	}
	
	public String getCounter(){
		return counter;
	}
	
	public void setCounter(String counter){
		this.counter = counter;
	}

	public String getExchange() {
		return exchange;
	}
	
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	@Override
	public int compareTo(Symbol o) {
		return this.symbol.compareTo(o.symbol);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symbol other = (Symbol) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String toString(){
		return symbol;
	}

}
