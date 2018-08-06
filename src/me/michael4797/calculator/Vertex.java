package me.michael4797.calculator;

/**
 * A 2D Point.
 */
public class Vertex {

	public final double x, y;
	
	/**
	 * A 2D point represented by the specified x and y coordinate.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Vertex(double x, double y) {
		
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public boolean equals(Object other) {
		
		if(other.getClass().equals(Vertex.class)) {
		
			Vertex v = (Vertex) other;
			return v.x == x && v.y == y;
		}
		
		return false;
	}
	
	
	@Override
	public String toString() {
		
		return "(" + x + "," + y + ")";
	}
}
