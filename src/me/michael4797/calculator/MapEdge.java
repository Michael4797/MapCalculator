package me.michael4797.calculator;

import java.util.ArrayDeque;

/**
 * Links MapNodes together based on shared LineDefs.
 */
public class MapEdge {

	private static final int PLAYER_HEIGHT = 56;	
	private final ArrayDeque<MapNode> front = new ArrayDeque<>(), back = new ArrayDeque<>();
	
	/**
	 * Checks if a player could travel between the specified MapNodes via this MapEdge.
	 * @param from The MapNode to travel from.
	 * @param to The MapNode to travel to.
	 * @return True if a player could travel from the specified MapNode to the specified MapNode.
	 */
	public boolean isTraversible(MapNode from, MapNode to) {
		
		if(from.ceiling - from.floor < PLAYER_HEIGHT)
			return false;	
		else if(to.ceiling - to.floor < PLAYER_HEIGHT)
			return false;	
		else if(from.ceiling - to.floor < PLAYER_HEIGHT)
			return false;	
		else if(to.ceiling - from.floor < PLAYER_HEIGHT)
			return false;
		
		return true;
	}
	
	
	/**
	 * Adds the specified MapNode to this MapEdge.
	 * @param node The node to be added.
	 */
	public void addMapNode(MapNode node) {
		
		if(front.isEmpty())
			front.add(node);
		else if(front.peek().geometry == node.geometry) //All MapNodes on one side of the LineDef share the same 2D geometry.
			front.add(node);
		else
			back.add(node);
	}
	
	/**
	 * Finds all the MapNodes that can be traversed to from the specified source node and adds them to the specified list.
	 * @param source The source MapNode.
	 * @param nodes The list to which all connected MapNodes should be added.
	 */
	public void addConnectedNodes(MapNode source, ArrayDeque<MapNode> nodes) {
		
		if(front.isEmpty())
			return;
		
		ArrayDeque<MapNode> destinations;
		if(front.peek().geometry == source.geometry)
			destinations = back;
		else if(!back.isEmpty())
			destinations = front;
		else
			return;
		
		for(MapNode dest: destinations)			
			if(!dest.visited && isTraversible(source, dest))
				nodes.add(dest);
	}
}
