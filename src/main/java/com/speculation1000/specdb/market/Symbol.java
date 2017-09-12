package com.speculation1000.specdb.market;

public class Symbol implements Comparable<Symbol> {
	
	private final String symbol;
	
	public Symbol(Market market){
		symbol = market.getBase()+market.getCounter()+":"+market.getExchange();
	}
	
	public Symbol(String base, String counter, String exchange){
		symbol = base+counter+":"+exchange;
	}
	
	public Symbol(String symbol){
		this.symbol = symbol;
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
