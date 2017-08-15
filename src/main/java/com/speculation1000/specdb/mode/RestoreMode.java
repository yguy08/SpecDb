package com.speculation1000.specdb.mode;

import java.util.logging.Level;

import com.speculation1000.specdb.dao.PoloniexDAO;
import com.speculation1000.specdb.db.CreateTable;
import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DropTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StartRun;

public class RestoreMode implements Mode {
	
    private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	public RestoreMode() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		StartRun.setStartRunTS();
		DropTable.dropTable();
		CreateTable.createTable(DbConnection.connect(DbConnectionEnum.SQLITE_MAIN));
		PoloniexDAO polo = new PoloniexDAO();
		
		getStartRunMessage();
		
		try {
			polo.restoreMarkets();
			specLogger.logp(Level.INFO, RestoreMode.class.getName(), "run", "Polo restore completed successfully!");
		} catch (SpecDbException e) {
			specLogger.logp(Level.SEVERE, RestoreMode.class.getName(), "run", "Polo restore failed!");
		}
		
	}

	@Override
	public String getStartRunMessage() {
		specLogger.logp(Level.INFO, RestoreMode.class.getName(), "run", "Polo Restore starting...");
		return null;
	}

	@Override
	public String getEndRunMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startApp() {
		run();
	}

}
