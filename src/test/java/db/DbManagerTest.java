package db;

import org.junit.Test;

public class DbManagerTest {
	
	@Test
	public void testConnect(){
		new DbManager().createTable();
	}
	/*
	@Test
	public void testGenericMarketQueryMaxDate(){
		String sql = "SELECT Max(Date) AS Date from markets";
		List<MarketDAO> marketList = new DbManager().genericMarketQuery(sql);
		boolean isDate = marketList.get(0).getDate() > 0;
		boolean otherNull = marketList.get(0).getSymbol() == null;
		//assertTrue(isDate && otherNull);
	}
	
	@Test
	public void test50daysFromDb(){
		List<MarketDAO> mDAO = new DbManager().getLast50Days();
		//assertTrue(mDAO.get(0).getDate() == SpecDbDate.getTodayUtcEpochSeconds());
		long lastDate = mDAO.get(mDAO.size()-1).getDate();
		long fiftyDaysAgo = SpecDbDate.getTodayUtcEpochSeconds() - 86400 * 50;
		boolean equal = lastDate == fiftyDaysAgo;
		//assertTrue(equal);
		System.out.println(mDAO.size());
	}
	
	@Test
	public void testTodayFromDb(){
		List<MarketDAO> mDAO = new DbManager().getToday();
		for(MarketDAO m : mDAO){
			long d1 = m.getDate();
			long d2 = SpecDbDate.getTodayUtcEpochSeconds();
			//assertTrue(m.getDate() == SpecDbDate.getTodayUtcEpochSeconds());
		}
	}*/

}
