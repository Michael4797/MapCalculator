package me.michael4797.parse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Convenience class used to read from a String like an InputStream.
 *
 */
public class StringStream extends InputStream{

	private final String data;
	private int index = 0;
	
	
	public StringStream(String data) {
		
		this.data = data;
	}

	
	@Override
	public int read() throws IOException {
	
		if(index < data.length())
			return data.charAt(index++);
		
		return -1;
	}
}
