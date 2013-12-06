package com.sciaps.common.hardware;

import org.apache.commons.lang.math.FloatRange;

import com.sciaps.common.hardware.Hardware;
import com.sciaps.common.hardware.Spectrum;
import com.sciaps.common.hardware.Hardware.RawSpectrumData;

public class MockRawSpectrumData implements Hardware.RawSpectrumData {

	private int mId;

	public MockRawSpectrumData(int i) {
		mId = i;
	}

	@Override
	public Spectrum getSpectrum() {
		return new Spectrum() {
			
			@Override
			public float[] getWavelengthValues(FloatRange wavelengthRange,
					int numSamples) {
				
				float[] retval = new float[2096];
				for(int i=0;i<2096;i++){
					retval[i] = mId;
				}
				
				return retval;
			}

			@Override
			public RawSpectrumData getRawSpectrumData() {
				return MockRawSpectrumData.this;
			}
		};
	}

}
