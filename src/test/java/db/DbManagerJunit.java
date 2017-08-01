package db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import dao.MarketDAO;

public class DbManagerJunit {
	
	@Test
	public void testGenericMarketQuery(){
		String sql = "SELECT Max(Date) AS Date from markets";
		List<MarketDAO> marketList = new DbManager().genericMarketQuery(sql);
		boolean isDate = marketList.get(0).getDate() > 0;
		boolean otherNull = marketList.get(0).getSymbol() == null;
		assertTrue(isDate && otherNull);
	}

}
