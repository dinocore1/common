package com.sciaps.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

public class HardwareTest {

	private MockLibsHardware mHardware;

	@Before
	public void setup() {
		mHardware = new MockLibsHardware();
	}
	
	@Test
	public void hardwareTest() throws Exception {
		
		ExecuteTest testContext = new ExecuteTest(mHardware, 10);
		
		OperationHandler handler = OperationHandler.getSingleton();
		Future<?> future = handler.addOperation(testContext);
		future.get();
	}
}
