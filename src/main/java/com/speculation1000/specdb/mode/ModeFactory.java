package com.speculation1000.specdb.mode;

public class ModeFactory {
	
	public static Mode getMode(String mode){
		switch(mode){
		case "quick":
		case "q":
			return new QuickMode();
		case "normal":
		case "n":
			return new NormalMode();
		case "r":
			return new RestorePoloMode();				
		default:
			return new QuickMode();
		}
	}

}
