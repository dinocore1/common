package com.sciaps.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Raw4SpectromerCSVFile {

	private static final Pattern sSplitRegex = Pattern.compile(",");

	private File mFile;
	private String[] mHeader;
	
	public Raw4SpectromerCSVFile(File f) throws IOException {
		mFile = f;
		
		BufferedReader reader = new BufferedReader(new FileReader(mFile));
		String line;
		
		//read header
		line = reader.readLine();
		mHeader = sSplitRegex.split(line);
		
		reader.close();
	}
	

	
	public ByteBuffer loadSpectromerter(int num) throws IOException {
		
		ArrayList<Float> rawvalues = new ArrayList<Float>();
		
		BufferedReader reader = new BufferedReader(new FileReader(mFile));
		//read header
		reader.readLine();
		
		String line;
		String[] values;
		
		while((line = reader.readLine()) != null){
			values = sSplitRegex.split(line);
			
			String strvalue = values[num].trim();
			rawvalues.add(Float.parseFloat(strvalue));
		}
		
		reader.close();
		
		//find the min
		float min = Float.POSITIVE_INFINITY;
		for(int i=0;i<rawvalues.size();i++){
			min = Math.min(rawvalues.get(i), min);
		}
		
		
		ByteBuffer retval = ByteBuffer.allocate(rawvalues.size()*2);
		ShortBuffer shortbuffer = retval.asShortBuffer();
		for(int i=0;i<rawvalues.size();i++){
			short v = (short) Math.round(rawvalues.get(i) - min);
			shortbuffer.put(v);
		}
		return retval;
	}
}
