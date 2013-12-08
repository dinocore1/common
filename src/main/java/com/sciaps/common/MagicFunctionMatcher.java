package com.sciaps.common;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.util.MathArrays;

public class MagicFunctionMatcher {

	public static class Rect {
		public float left;
		public float right;
		public float top;
		public float bottom;
		
		public Rect(float l, float r, float t, float b){
			this.left = l;
			this.right = r;
			this.top = t;
			this.bottom = b;
			
			if(l > r || b > t){
				throw new RuntimeException("not a valid rectangle");
			}
		}
		
		public float width() {
			return right - left;
		}
		
		public float height() {
			return top - bottom;
		}
	}
	
	private static int findYMax(float[] a, int start, int len){
		float yMax = Float.NEGATIVE_INFINITY;
		int x = 0;
		for(int i=start;i<len;i++){
			if(a[i] > yMax){
				yMax = a[i];
				x = i;
			}
		}
		return x;
	}
	
	
	
	
	public static double[] getScaleSpace(double[] data, float sigma){
		GaussianFunction f = GaussianFunction.normalized(0, sigma);
		
		float radius = sigma*3;
		int r = 2*((int)Math.ceil(radius)) + 1;
		double[] filter = new double[r];
		
		float xdiff = 2*radius / r;
		
		float total = 0;
		for(int i=0;i<filter.length;i++){
			filter[i] = f.value( i*xdiff - radius);
			total += filter[i];
		}
		
		for(int i=0;i<filter.length;i++){
			filter[i] /= total;
		}
		
		return Filter.filter(data, filter);
	}
	
	
	public double[] diffGaussian(double[] data, int octive, int k, int maxk) {
		return MathArrays.ebeSubtract(getScaleSpace(data, octive), getScaleSpace(data, 2*octive));
	}
	
	public static ArrayList<Integer> getKeyPoints(double[] data) {
		ArrayList<Integer> keypoints = new ArrayList<Integer>();
		
		for(int i=1;i<data.length-1;i++){
			if((data[i] > data[i+1] && data[i] > data[i-1]) ||
				(data[i] < data[i+1] && data[i] < data[i-1])) {
				//found peek or valley
				keypoints.add(i);
			}
		}
		return keypoints;
	}
	
	public MagicFunctionMatcher(float[] a, float[] b) {
		
		
		
	}
	
	
}
