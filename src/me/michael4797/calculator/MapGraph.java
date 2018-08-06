package me.michael4797.calculator;

import java.util.ArrayDeque;
import java.util.HashSet;

import me.michael4797.udmf.Block;
import me.michael4797.udmf.UniversalDoomMap;

/**
 * Constructs and traverses the graph data structure used to traverse the accessible areas of
 * a UDMF map and calculate its area.
 */
public class MapGraph {

	public final UniversalDoomMap map;
	private final ArrayDeque<MapNode> nodes = new ArrayDeque<>();
	private final ArrayDeque<MapNode> startingPoints = new ArrayDeque<MapNode>();
	
	/**
	 * Creates a graph data structure of the geographic areas of the specified
	 * UMDF map. The map will be traversed to calculate the area of the playable
	 * areas.
	 * @param map The UDMF map to be traversed.
	 * @param deathPits The sector tags whose area should be excluded.
	 */
	public MapGraph(UniversalDoomMap map, HashSet<Integer> deathPits) {
		
		this.map = map;
		
		SectorList sectors = new SectorList(map);
		
		ArrayDeque<LineDef> lines = new ArrayDeque<>();
		for(Block line: map.getBlocks("linedef")) {
		
			LineDef linedef = new LineDef(map, sectors, line); //Construct all the LineDefs and subsequently all the Sectors
			if(linedef.hasSpecial())
				lines.add(linedef); //If the line sets up a 3D floor, keep track of it so we can create our 3D floors after the Sectors are constructed
		}
		
		for(LineDef linedef: lines)
			linedef.performSpecial(sectors); //Setup the 3D floors
		
		lines = null;
				
		sectors.forEach((s) -> s.constructNodes(deathPits, nodes)); //Create MapNodes for each Sector
		
		for(Block thing: map.getBlocks("thing")) {
			
			if(thing.getInteger("type") == 11) { //We found a DM start
				
				Vertex v = new Vertex(thing.getFloat("x"), thing.getFloat("y"));
				double height = thing.getFloat("height");
				
				for(MapNode node: nodes) { //Add the MapNode containing this DM start to the startingPoints queue	
					if(node.contains(v, height)) {
						
						if(!startingPoints.contains(node))
							startingPoints.add(node);
						
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Traverses the MapNodes comprising this MapGraph and aggregates their areas.
	 * @return The total area, in map units, of the playable area in the map represented by this MapGraph.
	 */
	public double getArea() {
		
		ArrayDeque<MapNode> queue = new ArrayDeque<>(); //The queue used for the breadth first search
		queue.addAll(startingPoints); //Add the startingPoints to the queue
		
		double area = 0.0;
		while(!queue.isEmpty()) {
			
			MapNode node = queue.poll();
			if(node.visited)
				continue;
			
			area += node.getArea();
			node.visited = true;
			node.addConnectedNodes(queue); //Add the adjacent nodes to the queue
		}
		
		for(MapNode node: nodes)
			node.visited = false;
		
		return area;
	}
}
