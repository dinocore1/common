package com.sciaps.common.hardware;

public interface Hardware {
	
	public interface RawSpectrumData {
		Spectrum getSpectrum();
	}

	public interface SpectrumDataCallback {
		public void onRawSpectrumData(RawSpectrumData data);
	}
	
	public void addSpectrumDataCallback(SpectrumDataCallback cb);
	public void removeSpectrumDataCallback(SpectrumDataCallback cb);
	
	public void startTest();
}
