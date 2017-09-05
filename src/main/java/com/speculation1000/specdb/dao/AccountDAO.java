package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.TreeMap;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbNumFormat;

public class AccountDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static void updateAccountBalance(DbConnectionEnum dbce) throws SpecDbException{
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
		BigDecimal poloBalance = new PoloniexDAO().getAccountBalance();
		
		//get trex balance
		//BigDecimal trexBalance = new BittrexDAO().getAccountBalance();
		
		BigDecimal balance = SpecDbNumFormat.bdToEightDecimal(poloBalance); //plus trexBalance when implemented
		DeleteRecord.deleteAccountRecords(dbce, todayMidnight);
		InsertRecord.insertAccountBalance(dbce, balance, todayMidnight);
	}
	
	public static BigDecimal getCurrentAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		TreeMap<Long,BigDecimal> accountMap = getAccountBalanceMap(dbce,0);
		return accountMap.firstEntry().getValue();
	}
	
	public static TreeMap<Long,BigDecimal> getAccountBalanceMap(DbConnectionEnum dbce,int days) throws SpecDbException {
		Instant instant = Instant.now().minusSeconds(86400 * days);
		String sqlCommand = "SELECT * FROM Account WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
							+ " ORDER BY DATE DESC";
		Connection conn = DbConnection.connect(dbce);
		TreeMap<Long,BigDecimal> accountMap = QueryTable.genericAccountQuery(conn, sqlCommand);
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");
        //get bittrex balance
		try{
			conn.close();
		}catch(SQLException e){
			while (e != null) {
            	specLogger.logp(Level.SEVERE, MarketSummaryDAO.class.getName(), "getCurrentCloseList", e.getMessage());
	            e = e.getNextException();
	        }
		}
		return accountMap;
	}

}
