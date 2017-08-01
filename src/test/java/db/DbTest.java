package db;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class DbTest {
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(DbManagerJunit.class);
		
		for(Failure failure : result.getFailures()){
			System.out.println(failure.toString());
		}
		
		System.out.println(result.wasSuccessful());

	}

}
