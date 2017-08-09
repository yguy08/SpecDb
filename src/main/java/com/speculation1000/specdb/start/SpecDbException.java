package com.speculation1000.specdb.start;

import java.util.StringJoiner;

public class SpecDbException extends Exception {
	
	private static final long serialVersionUID = -5840499412907985652L;
	
	public SpecDbException(String message){
		super(message);
	}
	
	public static String exceptionFormat(StackTraceElement[] stackTraceElementArr){
		StringJoiner sj1 = new StringJoiner(":", "[", "]");
		for(StackTraceElement ste : stackTraceElementArr){
			StringJoiner sj2 = new StringJoiner(":", "[", "]");
			sj2.add(ste.getClassName());
			sj2.add(ste.getMethodName());
			sj2.add(String.valueOf(ste.getLineNumber()));
			sj1.add(sj2.toString());			
		}
		return sj1.toString();
	}
	
	

}
