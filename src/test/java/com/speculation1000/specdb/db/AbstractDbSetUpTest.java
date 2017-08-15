package com.speculation1000.specdb.db;

public class AbstractDbSetUpTest {
	
	public static void setUpTestDb(){
		
	}
	
	public static void main(String[] args){
		DbConnection.connect(DbConnectionEnum.H2_MAIN);
	}

}
