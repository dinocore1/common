package com.sciaps.common.math;

public class DiscreteDerivatives {

	
	public static double first(double[] data, int x) {
		return (data[x] - data[x-1]);
	}
	
	public static double second(double[] data, int x){
		return (data[x+1] - 2*data[x] + data[x-1]);
	}
}
