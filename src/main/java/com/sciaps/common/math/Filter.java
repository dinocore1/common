package com.sciaps.common.math;

public class Filter {

	public static float[] filter(float[] in, float[] filter) {
		final int xmax = (int) Math.floor(filter.length/2);
		float[] out = new float[in.length];
		
		for(int x=0;x<in.length;x++) {
			float sum = 0f;
			for(int xf=-xmax;xf<xmax;xf++){
				if (x-xf>=0&&x-xf<in.length) {
					sum += filter[xf+xmax]*in[x-xf];
				}
			}
			out[x] = sum;
		}
		
		return out;
	}
	
	public static double[] filter(double[] in, double[] filter) {
		final int xmax = (int) Math.floor(filter.length/2);
		double[] out = new double[in.length];
		
		for(int x=0;x<in.length;x++) {
			double sum = 0f;
			for(int xf=-xmax;xf<xmax;xf++){
				if (x-xf>=0&&x-xf<in.length) {
					sum += filter[xf+xmax]*in[x-xf];
				}
			}
			out[x] = sum;
		}
		
		return out;
	}
}
