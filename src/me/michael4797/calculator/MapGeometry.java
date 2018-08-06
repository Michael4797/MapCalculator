package me.michael4797.calculator;

import java.util.ArrayDeque;

/**
 * The geometry of a single MapNode. Represented as a combination of EdgeLoops.
 */
public class MapGeometry {

	public final EdgeLoop boundary;
	private final ArrayDeque<EdgeLoop> excludedArea = new ArrayDeque<>();
	
	/**
	 * Constructs the geometry for a MapNode based on the specified EdgeLoop.
	 * @param boundary The EdgeLoop that completely encompasses this MapGeometry.
	 */
	public MapGeometry(EdgeLoop boundary) {
		
		this.boundary = boundary;
	}
	
	/**
	 * Traverses all the lines in the EdgeLoops comprising this MapGeometry.
	 * Finds all viable links between the specified MapNode, represented by this MapGeometry, and other neighboring MapNodes.
	 * @param node The MapNode represented by this MapGeometry.
	 * @param edges The list to which MapEdges should be added.
	 */
	public void constructEdges(MapNode node, ArrayDeque<MapEdge> edges) {
		
		boundary.constructEdges(node, edges);
		for(EdgeLoop loop: excludedArea)
			loop.constructEdges(node, edges);
	}
	
	/**
	 * Excludes the area contained within the specified EdgeLoop from this MapGeometry.
	 * @param loop The excluded area.
	 */
	public void excludeArea(EdgeLoop loop) {
		
		excludedArea.add(loop);
	}
	
	/**
	 * Checks whether or not a single point in the specified EdgeLoop is contained within this MapGeometry.
	 * Assuming the EdgeLoop does not intersect with this MapGeometry, this method will return true if the
	 * specified EdgeLoop is completely inside of this EdgeLoop.
	 * @param loop The EdgeLoop to check.
	 * @return True if the specified EdgeLoop is contained within this MapGeometry.
	 */
	public boolean contains(EdgeLoop loop) {
			
		if(!boundary.contains(loop))
			return false;
		
		for(EdgeLoop excluded: excludedArea)
			if(excluded.contains(loop))
				return false;
		
		return true;
	}
	
	/**
	 * Tests whether the specified point is contained within this MapGeometry.
	 * @param point The point to test.
	 * @return True if the specified point is contained within this MapGeometry.
	 */
	public boolean contains(Vertex point) {
		
		if(!boundary.contains(point))
			return false;
		
		for(EdgeLoop excluded: excludedArea)
			if(excluded.contains(point))
				return false;
		
		return true;
	}
	
	/**
	 * Calculates the area, in map units, of this MapGeometry by subtracting the areas of the excluded
	 * EdgeLoops from the area of the bounding EdgeLoop.
	 * @return The area of this MapGeometry
	 */
	public double getArea() {
		
		double area = boundary.getArea();
		for(EdgeLoop excluded: excludedArea)
			area -= excluded.getArea();
		
		return area;
	}
}
