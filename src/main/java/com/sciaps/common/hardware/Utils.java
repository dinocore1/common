package com.sciaps.common.hardware;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Utils {

	/**
	 * Loads the data in a ByteBuffer into a int array. Note: this assumes that
	 * the underlying bytebuffer is using unsigned 16-bit numbers to hold raw pixel data.
	 * @param buffer
	 * @return
	 */
	public static int[] loadRawPixels(ByteBuffer buffer){
		ShortBuffer shortBuffer = buffer.asShortBuffer();
		
		int[] retval = new int[shortBuffer.capacity()];
		for(int i=0;i<retval.length;i++){
			retval[i] = shortBuffer.get();
		}
		return retval;
	}
}
