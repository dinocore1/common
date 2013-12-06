package com.sciaps.common;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class MexicanHatFunction implements UnivariateFunction {
	
	private final double a;
	private final double b;
	private final double c;
	private final double d;

	/**
	 * create mexican hat function with form:
	 * f(x) = a(1-(x^2/c^2))e^-((x-b)^2/(2c^2))+d
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public MexicanHatFunction(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public static MexicanHatFunction normalized(double mean, double sigma) {
		double a = 2.0 / ( Math.sqrt(3*sigma) * Math.pow( Math.PI, .25) );
		double b = mean;
		double c = sigma;
		double d = 0;
		
		return new MexicanHatFunction(a, b, c, d);
	}
	
	@Override
	public double value(double x) {
		double scale = a * (1.0-(((x-b)*(x-b))/(c*c)));
		double exp = Math.pow(Math.E, -( ((x-b)*(x-b))/(2.0*c*c) ) );
		return scale*exp+d;
	}

}
