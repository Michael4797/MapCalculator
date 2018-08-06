package me.michael4797.wad;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class WadFile {

	private final HashMap<String, byte[]> lumps = new HashMap<>();
	public final boolean iwad;
	
	/**
	 * Reads a WAD from the specified InputStream.
	 * @param data The InputStream to be read.
	 * @throws IOException If there is an error reading from the specified InputStream.
	 */
	public WadFile(InputStream data) throws IOException {
		
		ByteReader reader = new ByteReader(data);
		String fileType = reader.readString(4);
		
		if(fileType.equals("PWAD"))
			iwad = false;
		else if(fileType.equals("IWAD"))
			iwad = true;
		else
			throw new IOException("Invalid WAD file");
		
		int entries = reader.readInt();
		int directoryLocation = reader.readInt();
		
		//Since the directory can be anywhere in the WAD file, our data might be broken up into two parts.
		byte[] part1;
		byte[] part2;
		if(directoryLocation > 12) //If there's data before the directory, read it now
			part1 = reader.readByteArray(directoryLocation-12);
		else
			part1 = new byte[0];
		
		//Read the info for each lump from the directory
		int dataEnd = directoryLocation;
		LumpInfo[] info = new LumpInfo[entries];
		for(int i = 0; i < entries; ++i) {
		
			info[i] = new LumpInfo(reader.readInt(), reader.readInt(), reader.readString(8));
			
			int end = info[i].start + info[i].length;
			if(end > dataEnd)
				dataEnd = end;
		}
		
		int directorySize = (entries << 4);
		int position = directoryLocation + directorySize;
		if(dataEnd > position) //If there's data after the directory, read that now
			part2 = reader.readByteArray(dataEnd - position);
		else
			part2 = new byte[0];
		
		//Read each lump from our two parts of the data
		for(LumpInfo l: info)
			lumps.put(l.name.toUpperCase(), readEntry(l.start, l.length, directorySize, part1, part2));
	}
	
	/**
	 * Retrieves the lump with the specified name from this WAD file.
	 * @param name The name of the lump, case-insensitive.
	 * @return The byte array representation of this lump, or null if the lump does not exist.
	 */
	public byte[] getLump(String name) {
		
		return lumps.get(name.toUpperCase());
	}
	
	/**
	 * Reads a lump from the two chunks of the WAD file.
	 * @param start The location of the first byte in this lump.
	 * @param length The length of this lump, in bytes.
	 * @param directorySize The size of the directory.
	 * @param part1 The first half of our file's data (pre directory).
	 * @param part2 The second half of our file's data (post directory).
	 * @return A byte array representing this lump's data.
	 */
	private byte[] readEntry(int start, int length, int directorySize, byte[] part1, byte[] part2) {
		
		byte[] data = new byte[length];
		int position = 0;
		start -= 12;
		
		if(start < part1.length) {
			
			int end = start + length;
			for(int i = start; i < end; ++i)
				data[position++] = part1[i];
		}
		else {
			
			start -= directorySize + part1.length;
			int end = start + length;
			for(int i = start; i < end; ++i)
				data[position++] = part2[i];
		}
		
		return data;
	}
	
	/**
	 * Helper class for storing the data needed for reading a lump from a WAD file.
	 */
	private static class LumpInfo{
		
		private final int start;
		private final int length;
		private final String name;
		
		
		private LumpInfo(int start, int length, String name) {
			
			this.name = name;
			this.start = start;
			this.length = length;
		}
	}
}
