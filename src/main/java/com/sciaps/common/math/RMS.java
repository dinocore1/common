package com.sciaps.common.math;

public class RMS {
	
	private final int mNumValues;
	private float mSum;

	public RMS(int numValues) {
		mNumValues = numValues;
	}
	
	public void addValue(float value) {
		mSum += (1.0 / mNumValues) * (value * value);
	}
	
	public float getRMS() {
		return (float) Math.sqrt(mSum);
	}
	
	public static float calc(float[] a) {
		float sum = 0;
		for(int i=0;i<a.length;i++){
			sum += (1.0 / a.length) * (a[i] * a[i]);
		}
		return (float) Math.sqrt(sum);
		
	}

}
