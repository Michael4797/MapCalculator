package me.michael4797.calculator;

import java.util.function.Consumer;

import me.michael4797.udmf.UniversalDoomMap;

/**
 * Constructs, caches, and maintains Sector objects. Sectors are created lazily as
 * they are needed. Convenience functions are provided to help traverse existing Sectors.
 */
public class SectorList {

	private final UniversalDoomMap map;
	private final Sector[] sectors;
	
	/**
	 * Creates an empty array of Sectors to be initialized as needed.
	 * @param map The map whose sectors should be used.
	 */
	public SectorList(UniversalDoomMap map) {
		
		this.map = map;
		sectors = new Sector[map.getBlocks("sector").size()];
	}
	
	/**
	 * Finds or creates the Sector with the specified id.
	 * @param id The id of the Sector.
	 * @return The cached, or newly created Sector.
	 */
	public Sector getSector(int id) {
		
		if(id < 0 || id >= sectors.length)
			throw new IndexOutOfBoundsException();
		
		if(sectors[id] == null)
			sectors[id] = new Sector(map, map.getBlocks("sector").get(id));
		
		return sectors[id];
	}
	
	/**
	 * Traverses all existing Sectors and performs an operation on those with
	 * the specified tag.
	 * @param tag The tag to search for.
	 * @param action The action to perform.
	 */
	public void forEachWithTag(int tag, Consumer<Sector> action) {
		
		for(int i = 0; i < sectors.length; ++i)
			if(sectors[i] != null && sectors[i].tag == tag)
				action.accept(sectors[i]);
	}
	
	/**
	 * Performs an action on all cached Sectors.
	 * @param action The action to perform.
	 */
	public void forEach(Consumer<Sector> action) {
		
		for(int i = 0; i < sectors.length; ++i)
			if(sectors[i] != null)
				action.accept(sectors[i]);
	}
}
