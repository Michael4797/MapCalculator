package me.michael4797.calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import me.michael4797.udmf.Block;
import me.michael4797.udmf.UniversalDoomMap;

/**
 * Handles the geometric representation of a Sector and its 3D floors.
 */
public class Sector {

	private final ArrayList<Sector> floors;
	private final ArrayList<LineDef> lines;
	public final int floor, ceiling;
	public final int tag;
	
	/**
	 * Constructs a Sector from the specified Block's properties.
	 * @param map The map that this Sector resides in.
	 * @param sector The Block that represents this Sector.
	 */
	public Sector(UniversalDoomMap map, Block sector) {
		
		floors = new ArrayList<>();
		lines = new ArrayList<>();
		floor = sector.getInteger("heightfloor");
		ceiling = sector.getInteger("heightceiling");
		tag = sector.getInteger("id");
	}
	
	/**
	 * Adds a LineDef to this Sector's edges.
	 * @param line The line to be added.
	 */
	public void addLine(LineDef line) {
		
		lines.add(line);
	}
	
	/**
	 * Adds a 3DFloor as part of this Sector.
	 * @param sector The 3DFloor's control Sector.
	 */
	public void set3DFloor(Sector sector) {
		
		for(int i = 0; i < floors.size(); ++i) {
			if(floors.get(i).floor < sector.floor) {
				
				floors.add(i, sector);
				return;
			}
		}
		
		floors.add(sector);
	}
	
	/**
	 * Uses the LineDefs and 3DFloors of this Sector to construct MapNodes.
	 * @param deathPits The list of Sector tags representing death pits, used to check if this Sector or any
	 * 3D floors in this sector should have their area ignored.
	 * @param nodes The list into which all generated MapNodes will be inserted.
	 */
	public void constructNodes(HashSet<Integer> deathPits, ArrayDeque<MapNode> nodes) {
				
		HashMap<EdgeLoop, Boolean> inclusionList = new HashMap<>(); //Stores EdgeLoops and whether they should be included or excluded.
		while(!lines.isEmpty()) {
			
			EdgeLoop loop = new EdgeLoop();
			LineDef start = lines.remove(lines.size() - 1);
			loop.addNextPoint(start.v1, start);
			if(!findEdgeLoop(start.v2, loop))				
				continue;
			
			boolean included = true;
			for(EdgeLoop l: inclusionList.keySet()) {
				//Inclusion/exclusion is determined by checking which EdgeLoops lie within other EdgeLoops.
				if(l.contains(loop))
					included = !included;
				else if(loop.contains(l))
					inclusionList.put(l, !inclusionList.get(l));
			}
			
			inclusionList.put(loop, included);
		}
		
		ArrayDeque<MapGeometry> geometry = new ArrayDeque<>();
		for(Iterator<EdgeLoop> iterator = inclusionList.keySet().iterator(); iterator.hasNext();) {
			
			EdgeLoop loop = iterator.next();
			if(inclusionList.get(loop)) { //Each included area is a unique boundary, so should be represented by a unique MapGeometry
			
				geometry.add(new MapGeometry(loop));
				iterator.remove();
			}
		}
		//The remaining EdgeLoops are excluded area
		//For each of the remaining EdgeLoops, we need to find which MapGeometry it is contained within
		while(!inclusionList.isEmpty()) {
			
			boolean failed = true;
			for(Iterator<EdgeLoop> iterator = inclusionList.keySet().iterator(); iterator.hasNext();) {
				
				//Since it's possible to have an excluded area contained within multiple MapGeometries,
				//we need to check each excluded area against every MapGeometry and remove EdgeLoops one at a time
				EdgeLoop loop = iterator.next();
				MapGeometry parent = null;
				for(MapGeometry g: geometry) {
					
					if(g.contains(loop)) {
						
						if(parent == null)
							parent = g;
						else {
							//The EdgeLoop is contained within multiple MapGeometries, so we don't know what to do with it yet
							parent = null;
							break;
						}							
					}
				}
				
				if(parent != null) {
					//We found where one of the EdgeLoops goes, so we can keep going
					failed = false;
					parent.excludeArea(loop);
					iterator.remove();
				}
			}
			
			if(failed) {
				
				//We didn't find where any of the EdgeLoops went, but we still have EdgeLoops we haven't dealt with yet
				//Obviously this will produce an incorrect result, but print out a warning and keep going anyways
				System.out.println("Warning: failed to extract map geometry from sector");
				inclusionList.clear();
			}
		}
		
		//Each 3D floor splits this Sector vertically, so we need a separate MapNodes for each of these splits
		int floor = this.floor;
		Sector current = this;
		for(Sector s: floors) {
			
			int ceiling = s.floor;
			for(MapGeometry g: geometry)
				nodes.add(new MapNode(g, floor, ceiling, !deathPits.contains(current.tag), current != this)); //Construct the MapNodes for this vertical slice
			
			floor = s.ceiling;
			current = s;
		}
		
		int ceiling = this.ceiling;
		for(MapGeometry g: geometry)			
			nodes.add(new MapNode(g, floor, ceiling, !deathPits.contains(current.tag), current != this)); //Construct the MapNodes for the last vertical slice
	}
	
	/**
	 * Recursively builds an EdgeLoop with the lines in this Sector, using the specified Vertex as the connecting point.
	 * @param next The connecting Vertex.
	 * @param loop The EdgeLoop to be constructed.
	 * @return True if an EdgeLoop was constructed, false if no EdgeLoop exists.
	 */
	private boolean findEdgeLoop(Vertex next, EdgeLoop loop) {
		
		for(int i = 0; i < lines.size(); ++i) {
				
			LineDef line = lines.get(i);
			Vertex connecting; //The connecting vertex for the next segment
			if(line.v1.equals(next))
				connecting = line.v2;
			else if(line.v2.equals(next))
				connecting = line.v1;
			else
				continue;
			
			if(!loop.addNextPoint(next, line)) //Add the current segment to the EdgeLoop
				continue;
			
			lines.remove(i);
			if(connecting.equals(loop.getFirstPoint())) //We completed the EdgeLoop, so we're done
				return true;
			else if(findEdgeLoop(connecting, loop)) //We succeeded in building an EdgeLoop from this segment
				return true;
			//We failed at forming an EdgeLoop from this segment, so back track and try the next line
			loop.removeLastPoint();
			lines.add(i, line);
		}
		
		return false;
	}
}
