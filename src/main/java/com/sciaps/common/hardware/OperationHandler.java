package com.sciaps.common.hardware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationHandler {

	private static final Logger logger = LoggerFactory.getLogger(OperationHandler.class);
	
	private ExecutorService mThread = Executors.newSingleThreadExecutor();
	private Runnable mCurrentlyRunningTask;
	
	private static OperationHandler sSingleton;
	public static OperationHandler getSingleton() {
		if(sSingleton == null){
			sSingleton = new OperationHandler();
		}
		return sSingleton;
	}
	
	public Future<?> addOperation(final Runnable task) {
		return mThread.submit(new Runnable(){

			@Override
			public void run() {
				
				logger.debug("Starting hardware: " + task.getClass().getSimpleName());
				mCurrentlyRunningTask = task;
				try {
					task.run();
				} catch(Throwable t){
					logger.warn("Uncaught throwable: ", t);
				} finally {
					mCurrentlyRunningTask = null;
					logger.debug("Ended hardware operation: " + task.getClass().getSimpleName());
				}
			}
			
		});
	}
	
}
