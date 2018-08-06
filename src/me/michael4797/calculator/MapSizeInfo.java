package me.michael4797.calculator;

/**
 * The class used to handle organizing data regarding map sizes.
 */
public class MapSizeInfo implements Comparable<MapSizeInfo>{
	
	private final String name;
	private final double size;
	
	
	public MapSizeInfo(String name, double size) {
		
		this.name = name;
		this.size = size;
	}


	@Override
	public int compareTo(MapSizeInfo o) {

		//Order maps by their size
		return (int) Math.signum(size - o.size);
	}
	
	
	@Override
	public String toString() {
		
		return name + "\t" + size;
	}
}