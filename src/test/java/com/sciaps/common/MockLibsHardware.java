package com.sciaps.common;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MockLibsHardware implements Hardware {
	
	private ScheduledExecutorService mThreads = Executors.newSingleThreadScheduledExecutor();
	private HashSet<SpectrumDataCallback> mDataCallbacks = new HashSet<SpectrumDataCallback>();
	private boolean mIsTestInProgress = false;

	@Override
	public void addSpectrumDataCallback(SpectrumDataCallback cb) {
		mDataCallbacks.add(cb);

	}

	@Override
	public void removeSpectrumDataCallback(SpectrumDataCallback cb) {
		mDataCallbacks.remove(cb);

	}

	@Override
	public void startTest() {
		
		for(int i=0;i<20;i++){
			mThreads.schedule(createDataDelivery(i), i*10 + 10, TimeUnit.MILLISECONDS);
		}
	}
	
	Runnable createDataDelivery(final int i) {
		return new Runnable() {
			
			@Override
			public void run() {
				
				MockRawSpectrumData data = new MockRawSpectrumData(i);
				
				for(SpectrumDataCallback cb : mDataCallbacks){
					cb.onRawSpectrumData(data);
				}
				
			}
		};
	}

}
