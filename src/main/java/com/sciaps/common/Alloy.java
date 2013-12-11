package com.sciaps.common;

import java.util.ArrayList;
import java.util.HashMap;

public class Alloy {



    public class SpecRange {
		public final Element element;
		public final float min;
		public final float max;

        private SpecRange(Element e, float min, float max) {
            this.element = e;
            this.min = min;
            this.max = max;
        }

    }
	
	private final HashMap<Element, SpecRange> mChemicalSpec = new HashMap<Element, SpecRange>();
	public final String mName;
	
	public Alloy(String name) {
		mName = name;
	}

    public void addSpecRange(Element e, float min, float max) {
        mChemicalSpec.put(e, new SpecRange(e, min, max));
    }

    public Iterable<SpecRange> getSpec() {
        return mChemicalSpec.values();
    }

    public SpecRange getSpectForElement(Element element) {
        return mChemicalSpec.get(element);
    }
	
}
