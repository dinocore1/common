package com.sciaps.common.math;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class SumFunction implements UnivariateFunction {
	
	private UnivariateFunction[] mFunctions;

	public SumFunction(UnivariateFunction[] functions) {
		mFunctions = functions;
	}

	@Override
	public double value(double x) {
		double v = 0;
		
		for(UnivariateFunction f : mFunctions) {
			v += f.value(x);
		}
		
		return v;
	}

}
