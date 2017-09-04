package com.speculation1000.specdb.start;

import java.time.Instant;
import java.util.List;

import com.speculation1000.specdb.dao.AccountDAO;
import com.speculation1000.specdb.market.MarketStatusContent;
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
        sb.append("* App Running since: ");
        sb.append(SpecDbDate.instantToLogStringFormat(StartApp.getStartUpTs()));
        sb.append(" (Uptime: " + SpecDbTime.uptimePrettyStr(StartApp.getSystemUptime()) + ")\n");
        sb.append("* H2 DB Running since: ");
        sb.append(SpecDbDate.instantToLogStringFormat(DbServer.DB_START_UP_TS));
        sb.append(" (Uptime: " + SpecDbTime.uptimePrettyStr(DbServer.getSystemUptime()) + ")\n");
        sb.append("* H2 DB Status: ");
        sb.append(DbServer.getH2ServerStatus() + "\n");
        sb.append("********************************\n");
        return sb.toString();
	}

	public static String getTickerString(){
	    StringBuilder sb = new StringBuilder();
	    List<MarketStatusContent> marketList = MarketStatus.getMarketStatusList();
	    sb.append("********************************\n");
	    sb.append("          [ TICKERTAPE ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
	    for(MarketStatusContent m : marketList){
	    	sb.append(m.getSymbol() + " @" + m.getCurrentPrice() + "\n");
	    }
	    sb.append("********************************\n");
	    return sb.toString();		
	}
	
	public static String getLongEntriesString(){
		StringBuilder sb = new StringBuilder();
		List<MarketStatusContent> marketList = MarketStatus.getMarketStatusList();
	    sb.append("\n");
		sb.append("********************************\n");
	    sb.append("          [ HIGHS ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
	    for(MarketStatusContent m : marketList){
	    	if(m.getDayHighLowMap().firstEntry().getValue() >= 25){
	    		sb.append(m.getSymbol() + " @" + m.getCurrentPrice() + "\n");
	    	}
	    }
	    sb.append("********************************\n");
	    return sb.toString();
	}
	
	public static String getShortEntriesString(){
		StringBuilder sb = new StringBuilder();
		List<MarketStatusContent> marketList = MarketStatus.getMarketStatusList();
	    sb.append("\n");
		sb.append("********************************\n");
	    sb.append("          [ LOWS ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
	    for(MarketStatusContent m : marketList){
	    	if(m.getDayHighLowMap().firstEntry().getValue()<= -25){
	    		sb.append(m.getSymbol() + " @" + m.getCurrentPrice() + "\n");
	    	}
	    }
	    sb.append("********************************\n");
	    return sb.toString();
	}
	
	public static String getBalanceStr() throws SpecDbException{
		StringBuilder sb = new StringBuilder();
	    sb.append("\n");
		sb.append("********************************\n");
	    sb.append("          [ BALANCE ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
		sb.append(AccountDAO.getAccountBalance()+"\n");
	    sb.append("********************************\n");
	    return sb.toString();
	}
	
	public static String getOpenTradesStr() throws SpecDbException {
		StringBuilder sb = new StringBuilder();
	    sb.append("\n");
		sb.append("********************************\n");
	    sb.append("          [ OPEN TRADES ]\n");
	    sb.append(SpecDbDate.instantToLogStringFormat(Instant.now())+"\n");
		sb.append(AccountDAO.getOpenTrades()+"\n");
	    sb.append("********************************\n");
	    return sb.toString();		
	}

}
