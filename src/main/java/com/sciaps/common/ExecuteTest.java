package com.sciaps.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sciaps.common.Hardware.RawSpectrumData;
import com.sciaps.common.Hardware.SpectrumDataCallback;

public class ExecuteTest implements Runnable, SpectrumDataCallback {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteTest.class);
	
	public Hardware mHardware;
	private boolean mFinished;
	private ArrayList<RawSpectrumData> mSpectrumData = new ArrayList<RawSpectrumData>();

	private int mWaitForNumResults;
	
	public ExecuteTest(Hardware hw, int numResults) {
		mHardware = hw;
		mWaitForNumResults = numResults;
	}
	
	public List<RawSpectrumData> getResults() {
		return mSpectrumData;
	}

	@Override
	public void run() {

		try {
			
			logger.debug("Starting test");
			mFinished = false;

			mHardware.addSpectrumDataCallback(this);
			mHardware.startTest();

			waitForTestToEnd();

		} catch(InterruptedException e) {
			logger.warn("", e);
		} finally {
			mHardware.removeSpectrumDataCallback(this);
			logger.debug("End test");
		}

	}

	private synchronized void waitForTestToEnd() throws InterruptedException {
		while(!mFinished){
			wait();
		}

	}

	@Override
	public void onRawSpectrumData(RawSpectrumData data) {
		
		mSpectrumData.add(data);
		logger.debug("got RawSpectrumData " + mSpectrumData.size());
		
		if(mSpectrumData.size() >= mWaitForNumResults){
			mFinished = true;
		}
		
		synchronized(this){
			this.notify();
		}

	}
	
	public synchronized void abort() {
		mFinished = true;
		notifyAll();
	}

}
