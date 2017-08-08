package com.speculation1000.specdb.utils;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SpecDbTimeRunner {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(SpecDbTimeTest.class);
		
		for(Failure failure : result.getFailures()){
			System.out.println(failure.toString());
		}
		
		System.out.println(result.wasSuccessful());

	}

}
