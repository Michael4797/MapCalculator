package me.michael4797.calculator;

import java.util.ArrayDeque;

/**
 * A representation of a single geographical area of a map, represented by a single sector id.
 */
public class MapNode{
	
	private final ArrayDeque<MapEdge> edges = new ArrayDeque<>();
	public final MapGeometry geometry;
	public final int floor, ceiling;
	public final boolean playable;
	public final boolean threeDFloor;
	public boolean visited = false;
	
	/**
	 * Constructs a MapNode from the specified MapGeometry and Sector data. Constructs MapEdges between this MapNode and neighboring MapNodes.
	 * @param geometry The MapGeometry representing this node.
	 * @param floor The floor height of this node.
	 * @param ceiling The ceiling height of this node.
	 * @param playable True if this node is playable area, false if it is a death pit.
	 * @param threeDFloor True if the MapGeometry is divided vertically into multiple areas by 3D floors. False if this MapNode comprises the entire MapGeometry.
	 */
	public MapNode(MapGeometry geometry, int floor, int ceiling, boolean playable, boolean threeDFloor) {

		this.geometry = geometry;
		this.floor = floor;
		this.ceiling = ceiling;
		this.playable = playable;
		this.threeDFloor = threeDFloor;
		
		geometry.constructEdges(this, edges);
	}
	
	/**
	 * Checks if the specified 3D point is contained within this MapNode.
	 * @param point The 2D point to be tested.
	 * @param height The height of the point to be tested.
	 * @return True if the specified 3D point is contained within this MapNode.
	 */
	public boolean contains(Vertex point, double height) {
		
		if((!threeDFloor || height >= floor) && height < ceiling)
			return geometry.contains(point);
		
		return false;
	}
	
	/**
	 * Returns the 2D area represented by this MapNode, or 0 if this MapNode is not playable.
	 * @return The area of this MapNode, or 0 if this MapNode is not playable.
	 */
	public double getArea() {
		
		if(playable)
			return geometry.getArea();
		else
			return 0;
	}
	
	/**
	 * Adds all neighboring MapNodes to the specified list. Used when performing a traversal of the MapGraph.
	 * @param nodes The list of MapNodes to which neighboring MapNodes should be added.
	 */
	public void addConnectedNodes(ArrayDeque<MapNode> nodes) {

		for(MapEdge edge: edges)			
			edge.addConnectedNodes(this, nodes);
	}
}
