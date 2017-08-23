package com.speculation1000.specdb.start;

import java.time.Instant;
import java.util.List;

import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.time.SpecDbTime;

public class StatusString {
	
	public static String getSystemStatus(){
        StringBuilder sb = new StringBuilder();
        Instant now = Instant.now();
        sb.append("\n");
        sb.append("********************************\n");
        sb.append("          [SYSTEMSTATUS] \n");
        sb.append("* At: ");
        sb.append(SpecDbDate.instantToLogStringFormat(now) + "\n");
        sb.append("********************************\n");
        sb.append("                [APP]          \n");
        sb.append("* App Running since: ");
        sb.append(SpecDbDate.instantToLogStringFormat(StartApp.getStartUpTs()));
        sb.append("* App Uptime: " + SpecDbTime.uptimePrettyStr(StartApp.getSystemUptime()) + "\n");
        sb.append("********************************\n");
        sb.append("                [H2 DB]          \n");
        sb.append("* H2 DB Running since: ");
        sb.append(SpecDbDate.instantToLogStringFormat(DbServer.DB_START_UP_TS));
        sb.append("* H2 DB Uptime: " + SpecDbTime.uptimePrettyStr(DbServer.getSystemUptime()) + "\n");
        sb.append("* H2 DB Status: \n");
        sb.append("* " + DbServer.getH2ServerStatus() + " *\n");
        sb.append("********************************\n");
        return sb.toString();
	}

	public static String getTickerString(){
	    StringBuilder sb = new StringBuilder();
	    List<Market> marketList = MarketSummaryDAO.getTickerList(DbConnectionEnum.H2_MAIN);
	    sb.append("\n");
	    sb.append("********************************\n");
	    sb.append("          [ TICKERTAPE ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
	    for(Market m : marketList){
	    	sb.append(m.toString() + "\n");
	    }
	    sb.append("********************************\n");
	    return sb.toString();		
	}
	
	public static String getLongEntriesString(){
		StringBuilder sb = new StringBuilder();
		List<Market> marketList = MarketSummaryDAO.getMarketsAtXDayHigh(DbConnectionEnum.H2_MAIN, 25);
		sb.append("********************************\n");
	    sb.append("          [ LONG ENTRIES ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
	    for(Market m : marketList){
	    	sb.append(m.toString() + "\n");
	    }
	    sb.append("********************************\n");
	    return sb.toString();
	}
	
	public static String getShortEntriesString(){
		StringBuilder sb = new StringBuilder();
		List<Market> marketList = MarketSummaryDAO.getMarketsAtXDayLow(DbConnectionEnum.H2_MAIN, 25);
		sb.append("********************************\n");
	    sb.append("          [ SHORT ENTRIES ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
	    for(Market m : marketList){
	    	sb.append(m.toString() + "\n");
	    }
	    sb.append("********************************\n");
	    return sb.toString();
	}

}
