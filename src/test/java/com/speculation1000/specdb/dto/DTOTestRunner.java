package com.speculation1000.specdb.dto;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class DTOTestRunner {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(PoloniexDTOTest.class,BittrexDTOTest.class);
		
		for(Failure failure : result.getFailures()){
			System.out.println(failure.toString());
		}
		
		System.out.println(result.wasSuccessful());

	}

}
