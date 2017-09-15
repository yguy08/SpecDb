package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.TreeMap;
import java.util.logging.Level;

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
	
	public AccountDAO(DbConnectionEnum dbce) throws SpecDbException{
		//update account balance
		try{
			updateAccountBalance(dbce);
	    	specLogger.logp(Level.INFO, AccountDAO.class.getName(),"AccountDAO","Acocunt balance update successfully!");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, AccountDAO.class.getName(),"AccountDAO","Error updating account balance");
			throw new SpecDbException(e.getMessage());
		}		
	}
	
	public void updateAccountBalance(DbConnectionEnum dbce) throws SpecDbException{
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
		BigDecimal poloBalance;
		try{
			poloBalance = new PoloniexDAO().getAccountBalance(dbce);
			specLogger.logp(Level.INFO, AccountDAO.class.getName(),"updateAccountBalance","AccountBalance");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, AccountDAO.class.getName(),"AccountDAO","Error updating polo account balance");
			throw new SpecDbException(e.getMessage());
		}		
		
		//add trex
		BigDecimal balance = SpecDbNumFormat.bdToEightDecimal(poloBalance); //plus trexBalance when implemented
		DeleteRecord.deleteAccountRecords(dbce, todayMidnight);
		InsertRecord.insertAccountBalance(dbce, balance, todayMidnight);
	}
	
	public static BigDecimal getCurrentAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		String sqlCommand = "SELECT * FROM Account WHERE Date = (SELECT Max(Date) As Date FROM Account)";
		TreeMap<Long,BigDecimal> accountMap = QueryTable.genericAccountQuery(dbce, sqlCommand);
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");
        //get bittrex balance        
		return accountMap.firstEntry().getValue();
	}
	
	public static TreeMap<Long,BigDecimal> getAccountBalanceMap(DbConnectionEnum dbce,int days) throws SpecDbException {
		Instant instant = Instant.now().minusSeconds(86400 * days);
		String sqlCommand = "SELECT * FROM Account WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
							+ " ORDER BY DATE DESC";
		TreeMap<Long,BigDecimal> accountMap = QueryTable.genericAccountQuery(dbce, sqlCommand);
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");
        //get bittrex balance
		return accountMap;
	}

}
