package me.michael4797.calculator;

import me.michael4797.udmf.Block;
import me.michael4797.udmf.UniversalDoomMap;

/**
 * Handles the adjacency of Sectors and whether or not the line can be traversed.
 */
public class LineDef {

	private final Block line;
	private final boolean traversible;
	private MapEdge edge = null;
	
	public final Sector front, back;
	public final Vertex v1, v2;
	
	/**
	 * Constructs a LineDef from the properties of the specified Block.
	 * @param map The UDMF map this line resides in.
	 * @param sectors The list of Sectors.
	 * @param line The Block containing the properties for this LineDef.
	 */
	public LineDef(UniversalDoomMap map, SectorList sectors, Block line) {
				
		this.line = line;
		boolean twoSided = line.getBoolean("twosided") && line.getInteger("sideback") != -1;
		
		Block v1Block = map.getBlocks("vertex").get(line.getInteger("v1"));
		Block v2Block = map.getBlocks("vertex").get(line.getInteger("v2"));

		v1 = new Vertex(v1Block.getFloat("x"), v1Block.getFloat("y"));
		v2 = new Vertex(v2Block.getFloat("x"), v2Block.getFloat("y"));
		
		int id = map.getBlocks("sidedef").get(line.getInteger("sidefront")).getInteger("sector");
		front = sectors.getSector(id);
		
		if(twoSided) {
			
			id = map.getBlocks("sidedef").get(line.getInteger("sideback")).getInteger("sector");
			back = sectors.getSector(id);
			
			if(front != back) {
			
				front.addLine(this);
				back.addLine(this);
			}
			
			if(line.getBoolean("blocking") || line.getBoolean("blockplayers") || line.getBoolean("blockeverything"))
				traversible = false;
			else
				traversible = true;
		}
		else {
		
			back = null;
			traversible = false;
			front.addLine(this);
		}
	}
	
	/**
	 * True if this LineDef has the action special responsible for creating 3D floors.
	 * @return True if this LineDef represents the line of a 3D floor.
	 */
	public boolean hasSpecial() {
		
		return line.getInteger("special") == 160;
	}
	
	/**
	 * Transforms the Sector at the front side of this LineDef into a 3D floor based on the specified action special.
	 * @param sectors The list of Sectors.
	 */
	public void performSpecial(SectorList sectors) {
		
		int special = line.getInteger("special");
		if(special == 160) {

			int tag = line.getInteger("arg0");
			int type = line.getInteger("arg1");
			
			if((type&3) == 1)			
				sectors.forEachWithTag(tag, (s) -> s.set3DFloor(front));
		}
	}
	
	/**
	 * Constructs a MapEdge linking the front side Sectors and back side Sectors of this LineDef.
	 * @param node The MapNode that should be included in the resulting MapEdge.
	 * @return A MapEdge comprised of all MapNodes touching this LineDef, or null if this LineDef is not traversible.
	 */
	public MapEdge constructEdge(MapNode node) {
		
		if(!traversible)
			return null;
		
		if(edge == null)
			edge = new MapEdge();
		
		edge.addMapNode(node);
		return edge;
	}
}
