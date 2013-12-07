package com.sciaps.common;

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
	
	
	private static final int filterPixeles = 64;
	
	public static double[] getScaleSpace(double[] data, float sigma){
		GaussianFunction f = GaussianFunction.normalized(0, sigma);
		
		double[] filter = new double[filterPixeles];
		
		float xdiff = 4 / filterPixeles;
		
		for(int i=0;i<filterPixeles;i++){
			filter[i] = f.value( i*xdiff - 2);
		}
		
		return Filter.filter(data, filter);
	}
	
	
	private final float startOctive = 0.3f;
	public double[] diffGaussian(double[] data, int octive, int k, int maxk) {
		
		float octiveBaseSigma = octive * startOctive;
		
		return MathArrays.ebeSubtract(getScaleSpace(data, octiveBaseSigma + (k * startOctive/maxk)), getScaleSpace(data, octiveBaseSigma));
	}
	
	public MagicFunctionMatcher(float[] a, float[] b) {
		
		
		
	}
	
	
}
