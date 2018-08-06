package me.michael4797.calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * The primitive type that forms the geometry of the MapNodes.
 * Each edge loop is constructed as a series of vertices that encompass a MapNode.
 * The vertices are in either clockwise or counterclockwise order.
 */
public class EdgeLoop {

	private final ArrayList<Vertex> vertices = new ArrayList<>();
	private final ArrayList<LineDef> lines = new ArrayList<>();
	
	/**
	 * Retrieves the point that was used to start this EdgeLoop.
	 * @return The starting point of this EdgeLoop.
	 */
	public Vertex getFirstPoint() {
		
		return vertices.get(0);
	}
	
	/**
	 * Removes the point that was last added to this EdgeLoop.
	 */
	public void removeLastPoint() {
		
		vertices.remove(vertices.size() - 1);
		lines.remove(lines.size() - 1);
	}
	
	/**
	 * Adds the next point to this EdgeLoop. This will fail if the specified point is already contained in this EdgeLoop.
	 * @param point The next point in the EdgeLoop.
	 * @param line The line connecting the previous point to the specified point.
	 * @return True if the point was added, false if it could not be.
	 */
	public boolean addNextPoint(Vertex point, LineDef line) {
		
		if(vertices.contains(point))
			return false;
		
		vertices.add(point);
		lines.add(line);
		return true;
	}
	
	/**
	 * Traverses all the lines in this EdgeLoop and finds all viable links between the MapNode represented by this EdgeLoop and
	 * other neighboring MapNodes.
	 * @param node The MapNode bounded by this EdgeLoop.
	 * @param edges The list to which MapEdges should be added.
	 */
	public void constructEdges(MapNode node, ArrayDeque<MapEdge> edges) {
		
		for(LineDef line: lines) {
			
			MapEdge edge = line.constructEdge(node);
			if(edge != null)
				edges.add(edge);
		}
	}
	
	/**
	 * Checks whether or not a unique point in the specified EdgeLoop is contained within this EdgeLoop.
	 * Assuming the EdgeLoops do not intersect, this method will return true if the specified EdgeLoop is
	 * completely inside of this EdgeLoop.
	 * @param loop The EdgeLoop to check.
	 * @return True if the specified EdgeLoop is contained within this EdgeLoop.
	 */
	public boolean contains(EdgeLoop loop) {
		
		if(loop.vertices.isEmpty())
			return false;
		
		for(int i = 0; i < loop.vertices.size(); ++i) {
			
			if(!vertices.contains(loop.vertices.get(i)))
				return contains(loop.vertices.get(i));
		}
		
		if(getArea() >= loop.getArea())
			return true;
		
		return false;
	}
	
	/**
	 * Tests whether the specified point is contained within this EdgeLoop.
	 * @param point The point to test.
	 * @return True if the specified point is contained within this EdgeLoop.
	 */
	public boolean contains(Vertex point) {
		
		int i, j;
		boolean inside = false;
		for(i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
			
			if((vertices.get(i).y > point.y) != (vertices.get(j).y > point.y) &&
					point.x < (vertices.get(j).x - vertices.get(i).x) * (point.y - vertices.get(i).y) /
					(vertices.get(j).y - vertices.get(i).y) + vertices.get(i).x)
				inside = !inside;
		}
		
		return inside;
	}
	
	/**
	 * Calculates the area, in map units, contained within this EdgeLoop.
	 * @return The area of this EdgeLoop.
	 */
	public double getArea() {
		
		double area = 0;
		int j = vertices.size() - 1;
		for(int i = 0; i < vertices.size(); ++i) {
			
			area += (vertices.get(j).x+vertices.get(i).x) * (vertices.get(j).y-vertices.get(i).y);
			j = i;
		}
		
		return Math.abs(area/2);
	}
}
