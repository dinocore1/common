package com.sciaps.common;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.general.GaussNewtonOptimizer;
import org.junit.Assert;
import org.junit.Test;

import com.sciaps.common.utils.BasicRawSpectrum;

public class BasicRawSpectrumTest {
	
	private Spectrometer createSpectrometer1(){
		
		PolynomialFitter fitter = new PolynomialFitter(2, new GaussNewtonOptimizer());
		fitter.addObservedPoint(0, 100);
		fitter.addObservedPoint(20, 200);
		fitter.addObservedPoint(29, 300);
		Spectrometer spec = new Spectrometer(new FloatRange(100, 300), new PolynomialFunction(fitter.fit()));
		
		return spec;
	}
	
	private Spectrometer createSpectrometer2(){
		
		PolynomialFitter fitter = new PolynomialFitter(2, new GaussNewtonOptimizer());
		fitter.addObservedPoint(0, 280);
		fitter.addObservedPoint(20, 300);
		fitter.addObservedPoint(29, 400);
		Spectrometer spec = new Spectrometer(new FloatRange(280, 400), new PolynomialFunction(fitter.fit()));
		
		return spec;
	}

	
	@Test
	public void singleSpectrometerTest() {
		
		BasicRawSpectrum rawSpectrum = new BasicRawSpectrum();
		
		{
			Spectrometer spec = createSpectrometer1();
			ByteBuffer buffer = ByteBuffer.allocateDirect(30*2);
			ShortBuffer shortBuffer = buffer.asShortBuffer();
			for(int i=0;i<30;i++){
				shortBuffer.put((short) (i+1));
			}
			rawSpectrum.addData(spec, buffer);
		}
		
		Spectrum spectrum = rawSpectrum.getSpectrum();
		
		float[] wlvalues = spectrum.getWavelengthValues(new FloatRange(100, 300), 600);
		Assert.assertEquals(600, wlvalues.length);
		Assert.assertEquals(1, wlvalues[0], 0.03);
		Assert.assertEquals(30, wlvalues[wlvalues.length-1], 0.03);
		
		wlvalues = spectrum.getWavelengthValues(new FloatRange(100, 300), 1000);
		Assert.assertEquals(1000, wlvalues.length);
		Assert.assertEquals(1, wlvalues[0], 0.01);
		Assert.assertEquals(30, wlvalues[wlvalues.length-1], 0.1);
		
	}
	
	
}
