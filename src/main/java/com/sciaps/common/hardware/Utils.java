package com.sciaps.common.hardware;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Utils {

	public static float[] loadRawPixels(ByteBuffer buffer){
		ShortBuffer shortBuffer = buffer.asShortBuffer();
		
		float[] retval = new float[shortBuffer.capacity()];
		for(int i=0;i<retval.length;i++){
			retval[i] = shortBuffer.get();
		}
		return retval;
	}
}
