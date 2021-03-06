package com.sciaps.common.hardware;

import org.apache.commons.lang.math.FloatRange;

public interface Spectrum {

	public Hardware.RawSpectrumData getRawSpectrumData();
	public float[] getWavelengthValues(FloatRange wavelengthRange, int numSamples);
}
