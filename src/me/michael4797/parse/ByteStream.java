package me.michael4797.parse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Convenience class for treating a byte array as an InputStream
 */
public class ByteStream extends InputStream{

	private final byte[] data;
	private int index = 0;
	
	
	public ByteStream(byte[] data) {
		
		this.data = data;
	}

	
	@Override
	public int read() throws IOException {
	
		if(index < data.length)
			return data[index++];
		
		return -1;
	}
}
