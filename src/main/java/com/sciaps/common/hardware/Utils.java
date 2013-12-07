package com.sciaps.common.hardware;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Utils {

	public static short[] loadRawPixels(ByteBuffer buffer){
		ShortBuffer shortBuffer = buffer.asShortBuffer();
		
		short[] retval = new short[shortBuffer.capacity()];
		for(int i=0;i<retval.length;i++){
			retval[i] = shortBuffer.get();
		}
		return retval;
	}
}
