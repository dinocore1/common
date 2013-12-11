package com.sciaps.common;

import java.util.ArrayList;

import com.sciaps.common.hardware.Hardware;

public class TestResult {

	public final Hardware.RawSpectrumData rawSpectrumData;
	public final ArrayList<ChemResult> chemAnalysisResult = new ArrayList<ChemResult>();
    public final ArrayList<Alloy> alloyMatches = new ArrayList<Alloy>();
	
	public static class ChemResult {
		public Element element;
		public float value;
		public float error;
	}
	
	public TestResult(Hardware.RawSpectrumData rawdata) {
		rawSpectrumData = rawdata;
	}
}
