package com.sciaps.common.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.management.RuntimeErrorException;

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
		if(x.length != y.length){
			throw new RuntimeException("x y arrays must be same length");
		}
		RealMatrix W = new Array2DRowRealMatrix(x.length, 3);
		for(int i=0;i<x.length;i++){
			double[] row = new double[3];
			row[0] = 1;
			row[1] = x[i];
			row[2] = row[1]*row[1];
			W.setRow(i, row);
		}
		
		DecompositionSolver solver = new QRDecomposition(W).getSolver();
		
		ArrayRealVector yv = new ArrayRealVector(x.length);
		for(int i=0;i<y.length;i++){
			yv.setEntry(i, y[i]);
		}
		RealVector solution = solver.solve(yv);
		return new PolynomialFunction(solution.toArray());
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
	
	public class Solution {
		public ArrayList<KeyPoint> sourceKeypoints = new ArrayList<KeyPoint>();
		public ArrayList<KeyPoint> destKeypoints = new ArrayList<KeyPoint>();
		PolynomialFunction xmapper;
		double error;
	}
	
	public class PartialSolution {
		public PartialSolution(int[] src, int[] dest, double error) {
			System.arraycopy(src, 0, srcCluster, 0, 3);
			System.arraycopy(dest, 0, destCluster, 0, 3);
			this.error = error;
		}
		int[] srcCluster = new int[3];
		int[] destCluster = new int[3];
		double error;
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
			return String.format("[%d %s]", mX, Arrays.toString(getFingerprint()));
		}
		
	}
	
	private static class FingerprintDistance implements Comparator<KeyPoint> {

		private final KeyPoint mKeypoint;
		
		public FingerprintDistance(KeyPoint kp) {
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
	
	
	
	private static Comparator<KeyPoint> sKeypointX = new Comparator<KeyPoint>()  {

		@Override
		public int compare(KeyPoint o1, KeyPoint o2) {
			return Double.compare(o1.getX(), o2.getX());
		}
	};
	
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
	private PolynomialFunction mXmapper;
	private int modelOrder;
	
	
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
				
				if(kp.q > 0.2){
					keypoints.add(kp);
				}
			}
		}
		return keypoints;
	}
	
	private static ArrayList<KeyPoint> getNearestKeypoints(KeyPoint kp, ArrayList<KeyPoint> points) {
		ArrayList<KeyPoint> retval = new ArrayList<KeyPoint>(points);
		Collections.sort(retval, new FingerprintDistance(kp));
		return retval;
	}
	
	public MagicFunctionMatcher(double[] src, double[] dest, int modelOrder) {
		
		this.srcRaw = src;
		this.destRaw = dest;
		this.modelOrder = modelOrder;
		
		double[] srcdiff = diffGaussian(src, 12, 3, 3);
		mSrckeypoints = getKeyPoints(srcdiff);
		
		double[] destdiff = diffGaussian(dest, 12, 3, 3);
		mDestKeypoints = getKeyPoints(destdiff);
		
		
		int[] srcpts = new int[]{0,1,2};
		
		System.out.print("");
		
		
	}
	
	private void find3Cluster(ArrayList<KeyPoint> points, int[] result) {
		for(int i=result[2];i<points.size();i++){
			if(points.get(result[0]).getFingerprint()[0] < 0 &&
			 points.get(result[1]).getFingerprint()[0] > 0 &&
			 points.get(result[2]).getFingerprint()[0] < 0){
				return;
			}
			result[0] = i-2;
			result[1] = i-1;
			result[2] = i;
		}
	}
	
	
	private double findBestCluster(int[] src, int[] dest){
		int[] bestCluster = new int[3];
		System.arraycopy(dest, 0, bestCluster, 0, 3);
		double error = getSolutionError(src, dest);
		
		for(int i=dest[2]+1;i<mDestKeypoints.size();i++){
			dest[0] = i-2;
			dest[1] = i-1;
			dest[2] = i;
			double localError = getSolutionError(src, dest);
			if(localError < error){
				error = localError;
				bestCluster[0] = dest[0];
				bestCluster[1] = dest[1];
				bestCluster[2] = dest[2];
			}
		}
		
		System.arraycopy(bestCluster, 0, dest, 0, 3);
		return error;
	}

	
	public PolynomialFunction getXmapper() {
		return mXmapper;
	}
	
	private double calcError(PolynomialFunction xmapper) {
		double error = 0;
		ArrayList<KeyPoint> availableDestKP = new ArrayList<KeyPoint>(mDestKeypoints);
		for(KeyPoint kp : mSrckeypoints){
			double predicted = xmapper.value(kp.getX());
			double minDistance = Double.POSITIVE_INFINITY;
			int nearest = 0;
			for(int i=0;i<availableDestKP.size();i++){
				double distance = Math.abs(predicted - availableDestKP.get(i).getX());
				if(distance < minDistance){
					nearest = i;
					minDistance = distance;
				}
			}
			availableDestKP.remove(nearest);
			error += minDistance;
		}
		return error;
	}
	
	private double trySolution(ArrayList<KeyPoint> srcPts, ArrayList<KeyPoint> destPts) {
		
		RealMatrix W = new Array2DRowRealMatrix(srcPts.size(), 3);
		for(int i=0;i<srcPts.size();i++){
			double[] row = new double[3];
			row[0] = 1;
			row[1] = srcPts.get(i).getX();
			row[2] = row[1]*row[1];
			W.setRow(i, row);
		}
		
		DecompositionSolver solver = new QRDecomposition(W).getSolver();
		
		ArrayRealVector y = new ArrayRealVector(srcPts.size());
		for(int i=0;i<srcPts.size();i++){
			y.setEntry(i, destPts.get(i).getX());
		}
		RealVector solution = solver.solve(y);
		PolynomialFunction xmapper = new PolynomialFunction(solution.toArray());
		
		double error = calcError(xmapper);
		return error;
		
	}
	
	private double getSolutionError(int[] src, int[] dest) {
		RealMatrix W = new Array2DRowRealMatrix(3, 3);
		for(int i=0;i<3;i++){
			double[] row = new double[3];
			row[0] = 1;
			row[1] = mSrckeypoints.get(src[i]).getX();
			row[2] = row[1]*row[1];
			W.setRow(i, row);
		}
		
		DecompositionSolver solver = new QRDecomposition(W).getSolver();
		
		ArrayRealVector y = new ArrayRealVector(3);
		for(int i=0;i<3;i++){
			y.setEntry(i, mDestKeypoints.get(dest[i]).getX());
		}
		RealVector solution = solver.solve(y);
		PolynomialFunction xmapper = new PolynomialFunction(solution.toArray());
		
		double error = calcError(xmapper);
		return error;
	}
	
	
	
}
