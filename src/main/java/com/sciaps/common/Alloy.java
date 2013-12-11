package com.sciaps.common;

import java.util.ArrayList;

public class Alloy {

	public static class SpecRange {
		Element element;
		float min;
		float max;
	}
	
	public final ArrayList<SpecRange> mChemicalSpec = new ArrayList<SpecRange>();
	public final String mName;
	
	public Alloy(String name) {
		mName = name;
	}
	
}
