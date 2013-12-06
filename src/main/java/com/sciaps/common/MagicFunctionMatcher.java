package com.sciaps.common;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;

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
	
	public MagicFunctionMatcher(float[] a, float[] b) {
		
		
		
	}
	
	private double getMidpoint(float[] data, int start, int len){
		int xmax = findYMax(data, start, len);
		PolynomialFitter fitter = new PolynomialFitter(2, new LevenbergMarquardtOptimizer());
		fitter.addObservedPoint(start, a[0]);
		fitter.addObservedPoint(xmax, a[xmax]);
		fitter.addObservedPoint(len-1, 0);
		PolynomialFunction f = new PolynomialFunction(fitter.fit());
		double amidpoint = ExactRootSolver.linearRoot(f.polynomialDerivative());
	}
}
