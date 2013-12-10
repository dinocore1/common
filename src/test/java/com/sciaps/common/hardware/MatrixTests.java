package com.sciaps.common.hardware;



import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;

public class MatrixTests {


	@Test
	public void testSolveMatrix() {
		RealMatrix coefficients =
				new Array2DRowRealMatrix(new double[][] { 
						{ 1, 0, 0 }, 
						{ 1, -6, 36 }, 
						{ 1, 5, 25 } },
						false);
		
		
		DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(new double[]{-30, 0, 0}));
		
		Assert.assertArrayEquals(new double[]{-30, 1, 1}, solution.toArray(),0.001);
	}
	
	@Test
	public void leastSquareTest() {
		RealMatrix coefficients =
				new Array2DRowRealMatrix(new double[][] { 
						{ 1, -2, 4 }, 
						{ 1, 0.5, .25 }, 
						{ 1, -2.5, 6.25 },
						{ 1, -3.3, 10.89}},
						false);
		
		
		DecompositionSolver solver = new QRDecomposition(coefficients).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(new double[]{-25, -24, -26, -22}));
		
		Assert.assertArrayEquals(new double[]{-25.2, 2, 1}, solution.toArray(),0.2);
	}
	
	@Test
	public void afineMapTest() {
		
		RealMatrix A = new Array2DRowRealMatrix(new double[][]{
				{1, 0, 0},
				{0, 2, 0},
				{0, 0, 3}
		});
		
		DecompositionSolver solver = new QRDecomposition(A).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(new double[]{5, 10, 15}));
		
		Assert.assertArrayEquals(new double[]{5, 0, 0, 0}, solution.toArray(), 0.00001);
		
		
	}
}
