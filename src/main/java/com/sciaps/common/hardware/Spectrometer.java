package com.sciaps.common.hardware;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class Spectrometer {

	private UnivariateFunction mWavelengthMappingFunction;
	private FloatRange mWavelengthRange;
	
	public Spectrometer(FloatRange range, UnivariateFunction wavelengthmapping){
		mWavelengthRange = range;
		mWavelengthMappingFunction = wavelengthmapping;
	}
	
	public FloatRange getWavelengthRange() {
		return mWavelengthRange;
	}

	public UnivariateFunction getWavelengthMappingFunction() {
		return mWavelengthMappingFunction;
	}

	
	
	
}
