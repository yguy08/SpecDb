package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import com.speculation1000.specdb.StartRun;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.utils.SpecDbDate;

public class MarketSummaryDAO {
	
	public static List<Market> getLatest(){
		String sqlCommand = "SELECT * FROM markets WHERE date = (SELECT Max(Date) from markets)";
		List<Market> marketList = DbUtils.genericMarketQuery(sqlCommand);
		return marketList;
	}
	
	public static int getCountFromYesterdayToToday(Instant instant){
		long yesterday = SpecDbDate.getYesterdayEpochSeconds(SpecDbDate.getTodayMidnightInstant(instant));
		long today = SpecDbDate.getTodayUtcEpochSeconds(StartRun.getStartRunTS());
		String sqlCommand = "SELECT count(*) AS Count FROM markets WHERE date >"+" "+yesterday+" " + "AND date <"+" "+today+"";
		Connection connection = DbUtils.connect();
		int count = 0;
		try{
			Statement tmpStatement = connection.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            while(resultSet.next()){
            	count = resultSet.getInt("Count");
            }
            tmpStatement.close();
            connection.close();
            return count;
		}catch(SQLException ex){
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}
	}
	
	public static int getDistinctFromYesterdayToToday(Instant instant){
		long yesterday = SpecDbDate.getYesterdayEpochSeconds(SpecDbDate.getTodayMidnightInstant(instant));
		long today = SpecDbDate.getTodayUtcEpochSeconds(instant);
		String sqlCommand = "SELECT Count (DISTINCT Symbol) AS Count FROM markets WHERE date >"+" "+yesterday+" " + "AND date <"+" "+today+"";
		Connection connection = DbUtils.connect();
		int count = 0;
		try{
			Statement tmpStatement = connection.createStatement();
            ResultSet resultSet = tmpStatement.executeQuery(sqlCommand);
            while(resultSet.next()){
            	count = resultSet.getInt("Count");
            }
            tmpStatement.close();
            connection.close();
            return count;
		}catch(SQLException ex){
	        System.err.println("SQLException information");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
		}
	}
	
	public static void main(String[] args){
		System.out.println(getCountFromYesterdayToToday(Instant.now()));
		System.out.println(getDistinctFromYesterdayToToday(Instant.now()));
	}

}
