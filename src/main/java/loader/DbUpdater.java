package loader;

import java.io.IOException;

import db.DbManager;

public class DbUpdater {

	public static void main(String[] args) {
		new DbManager().createTable();
		
		try{
			PoloniexLoader.poloUpdater();
		}catch(Exception e){
			
		}
		
		try{
			new BittrexLoader();
		}catch(IOException e){
			
		}

	}

}
