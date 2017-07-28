package utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import loader.Connect;
import market.Market;

public class TrueRange {
	
	private String symbol;
	
	private long date;
	
	private BigDecimal trueRange;

	public TrueRange(String symbol, long date, BigDecimal trueRange) {
		this.symbol = symbol;
		this.date = date;
		this.trueRange = trueRange;
	}
	
	public TrueRange(){
		
	}
	
	public BigDecimal getTrueRange(){
		return this.trueRange;
	}
	
	public String getSymbol(){
		return this.symbol;
	}
	
	public long getDate(){
		return this.date;
	}	

	public static void main(String[] args) {
        String sql = "ALTER TABLE markets ADD COLUMN ATR decimal";
        try{
        	Connection conn = new Connect().getConnection();
        	PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("Table created...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		new TrueRange().setTrueRange();
		
	}
	
	public void setTrueRange(){
		PreparedStatement preparedStatement;
		Connection connection = new loader.Connect().getConnection();
		System.out.println("Connected!");
		try{	        
	        
			String compiledQuery = "SELECT DISTINCT Symbol from markets";
	        preparedStatement = connection.prepareStatement(compiledQuery);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        List<String> symbolList = new ArrayList<>();
	        while(rs.next()){
	        	symbolList.add(rs.getString("Symbol"));
	        }
			System.out.println("Got Symbols!");

	        for(String symbol : symbolList){
	        	compiledQuery = "Select Date,High,Low,Close from markets where symbol = ? order by Date ASC";
		        preparedStatement = connection.prepareStatement(compiledQuery);
	        	preparedStatement.setString(1, symbol);
	        	rs = preparedStatement.executeQuery();
	        	
	        	List<Long> dateList = new ArrayList<>();
	        	List<BigDecimal> high = new ArrayList<>();
	        	List<BigDecimal> low = new ArrayList<>();
	        	List<BigDecimal> close = new ArrayList<>();
	        	while(rs.next()){
	        		dateList.add(rs.getLong("Date"));
	        		high.add(rs.getBigDecimal("High"));
	        		low.add(rs.getBigDecimal("Low"));
	        		close.add(rs.getBigDecimal("Close"));
	        	}
				System.out.println("Got prices for " + symbol);

	        	List<TrueRange> trList = calcTrueRange(symbol,dateList,high,low,close);
	        	compiledQuery = "UPDATE markets SET ATR = ? WHERE Symbol = ? AND Date = ?";
		        preparedStatement = connection.prepareStatement(compiledQuery);
	        	for(TrueRange trueRange : trList){
		        	preparedStatement.setBigDecimal(1, trueRange.getTrueRange());
		        	preparedStatement.setString(2, trueRange.getSymbol());;
		        	preparedStatement.setLong(3,trueRange.getDate());
		        	preparedStatement.addBatch();
	        	}
	        	preparedStatement.executeBatch();
				System.out.println("Got true range for: " + symbol);
	        }
	        	        
	        preparedStatement.close();
        	connection.close();
			System.out.println("True Range set!");
        }catch(SQLException ex){
			System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}				
	}
	
	private List<TrueRange> calcTrueRange(String symbol, List<Long> dateList, 
			List<BigDecimal> high,List<BigDecimal> low,List<BigDecimal> close){
		
		int movingAvg = 20;		
		
		//set first TR for 0 position (H-L)
		BigDecimal tR = high.get(0).subtract(close.get(0)).abs();
		List<TrueRange> trueRangeList = new ArrayList<>();
		trueRangeList.add(new TrueRange(symbol,dateList.get(0),tR));
		
		for(int x = 1; x < movingAvg; x++){
			List<BigDecimal> trList = Arrays.asList(
					high.get(x).subtract(low.get(x).abs(), MathContext.DECIMAL32),
					high.get(x).subtract(close.get(x-1).abs(), MathContext.DECIMAL32),
					close.get(x-1).subtract(low.get(x).abs(), MathContext.DECIMAL32));				
				tR = tR.add(Collections.max(trList));
		}		
		tR = tR.divide(new BigDecimal(movingAvg), MathContext.DECIMAL32);		
		
		//initial up to MA get the same
		
		for(int x=1;x<movingAvg;x++){
			trueRangeList.add(new TrueRange(symbol,dateList.get(x),tR));
		}		
		
		//20 exponential moving average
		for(int x = movingAvg; x < close.size();x++){
			List<BigDecimal> trList = Arrays.asList(
					high.get(x).subtract(low.get(x).abs(), MathContext.DECIMAL32),
					high.get(x).subtract(close.get(x-1).abs(), MathContext.DECIMAL32),
					close.get(x-1).subtract(low.get(x).abs(), MathContext.DECIMAL32));					
					tR = tR.multiply(new BigDecimal(movingAvg - 1), MathContext.DECIMAL32)
					.add((Collections.max(trList)), MathContext.DECIMAL32).
					divide(new BigDecimal(movingAvg), MathContext.DECIMAL32);					
					trueRangeList.add(new TrueRange(symbol,dateList.get(x),tR));
		}
		
		return trueRangeList;
	}

}
