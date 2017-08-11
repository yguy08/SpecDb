package com.speculation1000.specdb.time;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SpecDbDateRunner {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(SpecDbDateTest.class);
		
		for(Failure failure : result.getFailures()){
			System.out.println(failure.toString());
		}
		
		System.out.println(result.wasSuccessful());

	}

}