package com.sciaps.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.MathArrays;

public class MagicFunctionMatcher {
	
	public static PolynomialFunction PolynomialFit2nd(double[] x, double[] y) {
		RealMatrix coefficients = new Array2DRowRealMatrix(new double[][]{
				{1, x[0], x[0]*x[0]},
				{1, x[1], x[1]*x[1]},
				{1, x[2], x[2]*x[2]}
		});
		DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(new double[]
				{
				y[0], 
				y[1],
				y[2]
				}));
		
		PolynomialFunction polynomial = new PolynomialFunction(solution.toArray());
		return polynomial;
	}
	
	public static PolynomialFunction PolynomialFit1st(double[] x, double[] y) {
		RealMatrix coefficients = new Array2DRowRealMatrix(new double[][]{
				{1, x[0]},
				{1, x[1]},
		});
		DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(new double[]
				{
				y[0], 
				y[1]
				}));
		
		PolynomialFunction polynomial = new PolynomialFunction(solution.toArray());
		return polynomial;
	}

	public static class KeyPoint {
		
		public final int mX;
		public double[] mData;
		public PolynomialFunction mPolynomial;
		private double mXHat;
		public double[] fingerprint;
		public double q;
		
		public KeyPoint(int i, double[] data) {
			mX = i;
			mData = data;
		}
		
		public double getX() {
			getInterpolator();
			return mXHat;
		}
		
		public double getY() {
			PolynomialFunction interp = getInterpolator();
			return interp.value(mXHat);
		}
		
		public PolynomialFunction getInterpolator() {
			if(mPolynomial == null){
				mPolynomial = PolynomialFit2nd(
						new double[]{mX-1, mX, mX+1}, 
						new double[]{mData[mX-1], mData[mX], mData[mX+1]});
				
				mXHat = ExactRootSolver.linearRoot(mPolynomial.polynomialDerivative());
			}
			return mPolynomial;
		}
		
		public double[] getFingerprint() {
			if(fingerprint == null) {
				getInterpolator();
				fingerprint = new double[4];
				fingerprint[0] = Math.atan(mPolynomial.derivative().value(mXHat-1));
				fingerprint[1] = Math.atan(mPolynomial.derivative().value(mXHat+1));
				
				fingerprint[2] = Math.abs(mPolynomial.value(mXHat-1) - mPolynomial.value(mXHat));
				fingerprint[3] = Math.abs(mPolynomial.value(mXHat+1) - mPolynomial.value(mXHat));
			}
			
			return fingerprint;
		}
		
		
		@Override
		public String toString() {
			return String.format("[%d %s]", mX, Arrays.toString(fingerprint));
		}
		
	}
	
	private static class KeyPointComparator implements Comparator<KeyPoint> {

		private final KeyPoint mKeypoint;
		
		public KeyPointComparator(KeyPoint kp) {
			mKeypoint = kp;
		}
		
		public static double getDistance(KeyPoint a, KeyPoint b){
			double d1 = a.fingerprint[0] - b.fingerprint[0];
			double d2 = a.fingerprint[1] - b.fingerprint[1];
			//double d3 = a.fingerprint[2] - b.fingerprint[2];
			
			return Math.sqrt(d1*d1 + d2*d2 /* + d3*d3 */);
		}

		@Override
		public int compare(KeyPoint o1, KeyPoint o2) {
			
			double d1 = getDistance(o1, mKeypoint);
			double d2 = getDistance(o2, mKeypoint);
			
			return Double.compare(d1, d2);
			
		}
		
	}
	
	private static class KeyPointXValueComparator implements Comparator<KeyPoint> {

		@Override
		public int compare(KeyPoint o1, KeyPoint o2) {
			return Double.compare(o1.getX(), o2.getX());
		}
		
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

	private double[] srcRaw;
	private double[] destRaw;
	private ArrayList<KeyPoint> mSrckeypoints;
	private ArrayList<KeyPoint> mDestKeypoints;
	
	
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
				
				KeyPoint kp = new KeyPoint(i, data);
				kp.q = Math.abs(kp.getY()) / max;
				
				if(kp.q > 0.20){
					keypoints.add(kp);
				}
			}
		}
		return keypoints;
	}
	
	private ArrayList<KeyPoint> getNearestKeypoints(KeyPoint kp, ArrayList<KeyPoint> points) {
		ArrayList<KeyPoint> retval = new ArrayList<KeyPoint>(points);
		Collections.sort(retval, new KeyPointComparator(kp));
		return retval;
	}
	
	public MagicFunctionMatcher(double[] src, double[] dest, int xorder) {
		
		this.srcRaw = src;
		this.destRaw = dest;
		
		double[] srcdiff = diffGaussian(src, 12, 3, 3);
		mSrckeypoints = getKeyPoints(srcdiff);
		
		double[] destdiff = diffGaussian(dest, 12, 3, 3);
		mDestKeypoints = getKeyPoints(destdiff);
		
		int min = Math.min(mSrckeypoints.size(), mDestKeypoints.size());
		int max = Math.max(mSrckeypoints.size(), mDestKeypoints.size());
		
		RealMatrix W = new Array2DRowRealMatrix(min, 3);
		for(int i=0;i<min;i++){
			double[] row = new double[3];
			row[0] = 1;
			row[1] = mSrckeypoints.get(i).getX();
			row[2] = row[1]*row[1];
			W.setRow(i, row);
		}
		
		DecompositionSolver solver = new QRDecomposition(W).getSolver();
		
		ArrayRealVector y = new ArrayRealVector(min);
		for(int i=0;i<min;i++){
			y.setEntry(i, mDestKeypoints.get(i).getX());
		}
		RealVector solution = solver.solve(y);
		PolynomialFunction xmapper = new PolynomialFunction(solution.toArray());
		
		double error = 0;
		for(int i=0;i<min;i++){
			double srcx = mSrckeypoints.get(i).getX();
			error += xmapper.value(srcx); 
		}
		
	}
	
	
	public double trySolution(ArrayList<KeyPoint> srcPts, ArrayList<KeyPoint> destPts) {
		
		PolynomialFunction xMapping = PolynomialFit2nd(
				new double[]{srcPts.get(0).getX(), srcPts.get(1).getX(), srcPts.get(2).getX()},
				new double[]{destPts.get(0).getX(), destPts.get(1).getX(), destPts.get(2).getX()});
		
		double srcmin, srcmax, destmin, destmax;
		srcmin = Double.POSITIVE_INFINITY;
		destmin = Double.POSITIVE_INFINITY;
		destmax = Double.NEGATIVE_INFINITY;
		srcmax = Double.NEGATIVE_INFINITY;
		
		for(KeyPoint k : srcPts){
			srcmin = Math.min(srcmin, k.getY());
			//src
		}
		
		PolynomialFunction yMapping = PolynomialFit1st(
				new double[]{srcPts.get(0).getY(), srcPts.get(1).getY()},
				new double[]{destPts.get(0).getY()});
		
		double error = 0;
		for(int i=0;i<mSrckeypoints.size();i++){
			KeyPoint skp = mSrckeypoints.get(i);
			if(!srcPts.contains(skp)){
				double actual = skp.getX();
				double estimate = xMapping.value( skp.getX() );
				
			}
			int destx = (int) Math.round( xMapping.value(i) );
			
			double ysrc = srcRaw[i];
			double ydest = destRaw[destx];
			
			error += Math.abs(ydest - ysrc) * 1.0/srcRaw.length;
		}
		
		return error;
		
	}
	
	
	
	
}
