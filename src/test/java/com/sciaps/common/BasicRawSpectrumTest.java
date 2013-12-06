package com.sciaps.common;

import java.nio.ByteBuffer;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.general.GaussNewtonOptimizer;
import org.junit.Test;

import com.sciaps.common.utils.BasicRawSpectrum;

public class BasicRawSpectrumTest {

	
	@Test
	public void rawSpecTest() {
		
		BasicRawSpectrum rawSpectrum = new BasicRawSpectrum();
		
		{
			PolynomialFitter fitter = new PolynomialFitter(2, new GaussNewtonOptimizer());
			fitter.addObservedPoint(250, 185);
			fitter.addObservedPoint(270, 187);
			fitter.addObservedPoint(260, 300);
			Spectrometer spec = new Spectrometer(new FloatRange(185, 260), new PolynomialFunction(new double[]{2,3,4}));
			
			ByteBuffer buffer = ByteBuffer.allocateDirect(300*2);
			rawSpectrum.addData(spec, buffer);
		}
		
		Spectrum spectrum = rawSpectrum.getSpectrum();
		spectrum.getWavelengthValues(new FloatRange(185, 290), 50);
		
		
		
	}
}
