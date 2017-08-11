package com.speculation1000.specdb.start;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.speculation1000.specdb.dao.MarketSummaryDAOTest;
import com.speculation1000.specdb.db.DbTest;
import com.speculation1000.specdb.time.SpecDbDateTest;
import com.speculation1000.specdb.time.SpecDbTimeTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   DbTest.class,
   SpecDbDateTest.class,
   SpecDbTimeTest.class,
   StartRunTest.class,
   MarketSummaryDAOTest.class
})

public class SpecDbSuite {}
