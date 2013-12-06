package com.sciaps.common.utils;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.Pair;

import com.sciaps.common.Hardware;
import com.sciaps.common.Hardware.RawSpectrumData;
import com.sciaps.common.Spectrometer;
import com.sciaps.common.Spectrum;

public class BasicRawSpectrum implements Hardware.RawSpectrumData {

	ArrayList<Pair<Spectrometer, ByteBuffer>> mSpectrumBuffers = new ArrayList<Pair<Spectrometer, ByteBuffer>>();
	
	public void addData(Spectrometer spec, ByteBuffer buffer) {
		mSpectrumBuffers.add(new Pair<Spectrometer, ByteBuffer>(spec, buffer));
	}
	
	public static final Comparator<Pair<Spectrometer, ByteBuffer>> sStartRangeComparator = new Comparator<Pair<Spectrometer, ByteBuffer>>() {
		
		@Override
		public int compare(Pair<Spectrometer, ByteBuffer> o1, Pair<Spectrometer, ByteBuffer> o2) {
			return Float.compare(o1.getFirst().getWavelengthRange().getMinimumFloat(), o2.getFirst().getWavelengthRange().getMinimumFloat());
		}
	};
	

	@Override
	public Spectrum getSpectrum() {
		return new Spectrum() {

			@Override
			public float[] getWavelengthValues(FloatRange wavelengthRange, int numSamples) {
				
				Collections.sort(mSpectrumBuffers, sStartRangeComparator);
				
				
				float[] retval = new float[numSamples];
				final float descreteSize = (wavelengthRange.getMaximumFloat() - wavelengthRange.getMinimumFloat()) / numSamples;
				
				final float maxRequestedRange = wavelengthRange.getMaximumFloat();
				float x = wavelengthRange.getMinimumFloat();
				int i = 0;
				
				for(Pair<Spectrometer, ByteBuffer> pair : mSpectrumBuffers){
					Spectrometer spectrometer = pair.getFirst();
					ByteBuffer buffer = pair.getSecond();
					
					FloatRange spectrometerRange = spectrometer.getWavelengthRange();
					if(wavelengthRange.overlapsRange(spectrometerRange)){
						PolynomialSplineFunction spline = getSplineInterpolator(spectrometer, buffer);
						final float localMax = Math.min(spectrometerRange.getMaximumFloat(), maxRequestedRange);
						while(x < localMax && i < retval.length){
							retval[i++] = (float) spline.value(x);
							x += descreteSize;
						}
					}
				}
				
				return retval;
			}

			@Override
			public RawSpectrumData getRawSpectrumData() {
				return BasicRawSpectrum.this;
			}
		};
	}
	
	public static PolynomialSplineFunction getSplineInterpolator(Spectrometer spectrometer, ByteBuffer buffer) {
		ShortBuffer shortbuf = buffer.asShortBuffer();
		
		//load the values in from the buffer
		double[] y = new double[shortbuf.capacity()];
		for(int i=0;i<y.length;i++){
			y[i] = shortbuf.get(i);
		}

		//load the x values in wavelength domain
		double[] x = new double[y.length];
		for(int i=0;i<y.length;i++){
			x[i] = (float) spectrometer.getWavelengthMappingFunction().value(i);
		}
		SplineInterpolator interpolator = new SplineInterpolator();
		return interpolator.interpolate(x, y);
	}

	public static float[] getWavelengthValues(Spectrometer spectrometer, ByteBuffer buffer, FloatRange range, int numSamples) {

		if(!spectrometer.getWavelengthRange().containsRange(range)){
			throw new RuntimeException("spectrometer does not contain requested range");
		}

		
		PolynomialSplineFunction spline = getSplineInterpolator(spectrometer, buffer);

		float[] retval = new float[numSamples];
		final float descreteSize = (range.getMaximumFloat() - range.getMinimumFloat()) / numSamples;

		float x = range.getMinimumFloat();
		for(int i=0;i<retval.length;i++){ 
			retval[i] = (float) spline.value(x); 
			x += descreteSize;
		}

		return retval;
	}

}
