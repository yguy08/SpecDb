package com.speculation1000.specdb.start;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Test;

public class StartRunTest {

	@Test
	public void testStartRunTS(){
		StartRun.setStartRunTS();
		Instant startRun = StartRun.getStartRunTS();
		Instant endRun = StartRun.getStartRunTS().plusSeconds(15*60);
		assertTrue(endRun.getEpochSecond() - startRun.getEpochSecond() == 15*60);
	}
	
}
