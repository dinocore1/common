package com.sciaps.common;

public class MockRawSpectrumData implements Hardware.RawSpectrumData {

	private int mId;

	public MockRawSpectrumData(int i) {
		mId = i;
	}

	@Override
	public Spectrum getSpectrum() {
		return new Spectrum() {
			
			@Override
			public float[] getWavelengthValues(float startWavelength, float endWavelen,
					int numSamples) {
				
				float[] retval = new float[2096];
				for(int i=0;i<2096;i++){
					retval[i] = mId;
				}
				
				return retval;
			}
		};
	}

}
