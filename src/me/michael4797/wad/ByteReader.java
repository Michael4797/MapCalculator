package me.michael4797.wad;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Convenience class used to read binary data from a WAD file.
 */
public class ByteReader {

	private final InputStream in;
	private byte[] data;
	private int index;
	private int limit;
	
	public ByteReader(InputStream in){
		
		this.in = in;
		data = new byte[8192];
		index = 0;
		limit = 0;
	}
	
	public ByteReader(byte[] data){
		
		this.data = data;
		in = null;
		index = 0;
		limit = this.data.length;
	}
	
	public ByteReader(byte[] data, int offset, int length){
		
		this.data = data;
		in = null;
		limit = offset+length;
		index = offset;
	}
	
	
	private boolean readBlock() {
		
		if(in == null)
			return false;
		
		index = 0;
		try {
			
			limit = in.read(data, 0, data.length);
		} catch (IOException e) {
			
			throw new RuntimeException("Error reading underlying channel", e);
		}
		
		if(limit == -1)
			return false;
		
		return true;
	}
	
	
	public boolean hasMoreData(){
		
		if(index >= limit)
			return readBlock();
		
		return true;
	}
	
	
	public byte[] getData(){
		
		return Arrays.copyOfRange(data, 0, limit);
	}
	
	
	public byte readByte(){
		
		if(!hasMoreData())
			throw new RuntimeException("End of stream");

		return data[index++];
	}
	
	
	public byte[] readByteArray(int size){
		
		byte[] data = new byte[size];
		for(int i = 0; i < data.length; i++)
			data[i] = readByte();
		
		return data;
	}
	
	
	public boolean readBoolean(){
		
		return readByte() != 0;
	}
	
	
	public short readShort(){
		
		return (short) ((readByte()&255)+((readByte()&255)<<8));
	}
	
	
	public int readInt(){
		
		return (readShort()&65535)+((readShort()&65535)<<16);
	}
	

	public long readLong() {

		return (readInt()&4294967295L)+((readInt()&4294967295L)<<32);
	}
	
	
	public float readFloat(){
		
		return Float.intBitsToFloat(readInt());
	}
	
	
	public String readString(int width){
		
		String string = "";
		boolean end = false;
		for(int i = 0; i < width; i++){
			
			char c = (char) readByte();
			
			if(end)
				continue;
			
			if(c == 0)
				end = true;
			else
				string += c;
		}
		
		return string;
	}
	
	
	public String readString(){
		
		String string = "";
		for(;;){
			
			char c = (char) readByte();
			if(c == 0)
				break;
			
			if(string.length() < 2047)
				string += c;
		}
		
		return string;
	}
}
