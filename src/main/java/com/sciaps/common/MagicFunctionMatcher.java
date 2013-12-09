package com.sciaps.common;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.util.MathArrays;

public class MagicFunctionMatcher {

	public static class KeyPoint {
		double xhat;
		public double q;
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
	
	public static ArrayList<KeyPoint> getKeyPoints(double[] data) {
		ArrayList<KeyPoint> keypoints = new ArrayList<KeyPoint>();
		
		double max = Double.NEGATIVE_INFINITY;
		for(int i=0;i<data.length;i++){
			max = Math.max(max, Math.abs(data[i]));
		}
		
		for(int i=1;i<data.length-1;i++){
			if((data[i] > data[i+1] && data[i] > data[i-1]) ||
				(data[i] < data[i+1] && data[i] < data[i-1])) {
				//found local extrema (peek or valley)
				
				PolynomialFunction f = new PolynomialFunction(new double[]{
						data[i], 
						DiscreteDerivatives.first(data, i),
						0.5*DiscreteDerivatives.second(data, i)
				});
				
				PolynomialFunction f1 = f.polynomialDerivative();
				double xhat = ExactRootSolver.linearRoot(f1);
				double q = Math.abs(f.value(xhat)) / max;
				
				if(q > 0.25){
					KeyPoint kp = new KeyPoint();
					kp.xhat = xhat;
					kp.q = q;
					keypoints.add(kp);
				}
			}
		}
		return keypoints;
	}
	
	public MagicFunctionMatcher(double[] src, double[] dest, int xorder) {
		
		double[] srcdiff = diffGaussian(src, 12, 3, 3);
		getKeyPoints(srcdiff);
		
		double[] destdiff = diffGaussian(dest, 12, 3, 3);
		getKeyPoints(destdiff);
		
		
		
	}
	
	
}
