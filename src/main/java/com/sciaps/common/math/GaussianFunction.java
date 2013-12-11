package com.sciaps.common.math;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class GaussianFunction implements UnivariateFunction {

	private final double a, b, c, d;
	
	/**
	 * Create a Gaussian function of the form
	 * f(x) = a e ^-((x-b)^2)/(2c^2) + d
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public GaussianFunction(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public static GaussianFunction normalized(double mean, double sigma) {
		double a = 1 / (sigma * Math.sqrt(2*Math.PI));
		return new GaussianFunction(a, mean, sigma, 0);
	}
	

	@Override
	public double value(double x) {
		return (a * Math.pow(Math.E, - (((x-b)*(x-b) )/( 2*c*c )) ) ) + d;
	}
}
