package me.michael4797.calculator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.michael4797.acs.ScriptFile;
import me.michael4797.calculator.visitor.MapScriptsVisitor;
import me.michael4797.udmf.UniversalDoomMap;
import me.michael4797.wad.WadFile;

/**
 * Calculates the size of UDMF Doom maps in map units found in a specified pk3 file.
 * Only the 'playable area' is calculated. This excludes any death pits or inaccessible
 * areas of the map.
 */
public class MapSizeCalculator{

	public static void main(String[] args) throws IOException {

		if(args.length == 0) {
			
			System.out.println("Specify the pk3 file to calculate as a command line argument");
			return;
		}
		
		ZipFile file = new ZipFile(args[0]);
		Enumeration<? extends ZipEntry> entries = file.entries();
		MapScriptsVisitor visitor = new MapScriptsVisitor();
		
		PriorityQueue<MapSizeInfo> queue = new PriorityQueue<>();
		while(entries.hasMoreElements()) {
			
			ZipEntry entry = entries.nextElement();
			if(entry.getName().endsWith(".wad")) {
				
				//We found a wad in the pk3
				WadFile wad = new WadFile(file.getInputStream(entry));
				if(wad.getLump("textmap") != null) {
					
					//We found a UMDF map
					UniversalDoomMap map = new UniversalDoomMap(wad);
					
					HashSet<Integer> deathPits;
					if(wad.getLump("scripts") != null) {
						
						//The UDMF map has scripts we can parse
						ScriptFile scripts = new ScriptFile(wad);
						deathPits = scripts.getAbstractSyntaxTree().accept(visitor);
					}
					else {
						
						deathPits = new HashSet<Integer>();
					}
					
					MapGraph graph = new MapGraph(map, deathPits);
					double area = graph.getArea();
					//Print some progress info so we know we're not stuck forever
					System.out.println(map.mapCode + '\t' + map.mapName + "\tcompleted");
								
					queue.add(new MapSizeInfo(String.format("%-8s\t%-22s", map.mapCode, map.mapName), area));
				}
			}
		}
		
		file.close();

		System.out.println("Maps By Size: ");
		while(!queue.isEmpty())
			System.out.println(queue.poll());		
	}
}
