package com.speculation1000.specdb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.DbServer;

public class AbstractDbSetUpTest {
	
	public static void setUpTestDb(){
		
	}
	
	public static void main(String[] args){
		try {
			DbServer.startDB();
			System.out.println("db server started!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try{
			Connection conn = DbConnection.connect(DbConnectionEnum.H2_MAIN);
			List<Market> marketList = new ArrayList<>();
			for(int i = 0; i < 10; i++){
				Market m = new Market();
				m.setBase("BTC");
				m.setCounter("USDT");
				m.setDate(159000000);
				m.setExchange("POLO");
				marketList.add(m);
			}
			
			CreateTable.createTable(conn);
			InsertRecord.insertBatchMarkets(conn, marketList);
			String sql = "SELECT * from Markets";
			List<Market> dbList = new ArrayList<>();
			dbList = QueryTable.genericMarketQuery(conn, sql);
			for(Market m : dbList){
				System.out.println(m.toString());
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}

}
