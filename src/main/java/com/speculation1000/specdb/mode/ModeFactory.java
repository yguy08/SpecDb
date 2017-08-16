package com.speculation1000.specdb.mode;

public class ModeFactory {
	
	public static Mode getMode(String mode){
		switch(mode){
		case "quick":
		case "q":
			return new QuickMode();
		case "expert":
		case "e":
			return new ExpertMode();
		case "standard":
		case "s":
			return new StandardMode();
		case "restore":
		case "r":
			return new RestoreMode();
		default:
			return new StandardMode();
		}
	}

}
