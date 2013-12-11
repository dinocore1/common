package com.sciaps.common;

import java.util.ArrayList;

import com.sciaps.common.hardware.Hardware;

public class TestResult {

	public final Hardware.RawSpectrumData mRawSpectrumData;
	public final ArrayList<Assay> mChemAnalysisResult = new ArrayList<Assay>();
	
	public static class Assay {
		Element element;
		float value;
		float error;
	}
	
	public TestResult(Hardware.RawSpectrumData rawdata) {
		mRawSpectrumData = rawdata;
	}
}
