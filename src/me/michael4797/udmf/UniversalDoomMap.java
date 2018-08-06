package me.michael4797.udmf;

import java.util.ArrayList;
import java.util.HashMap;

import me.michael4797.parse.ByteStream;
import me.michael4797.parse.Grammar;
import me.michael4797.parse.ParserException;
import me.michael4797.parse.ParserToken;
import me.michael4797.wad.WadFile;

/**
 * All the information representing a Map in UDMF format.
 */
public class UniversalDoomMap {

	private static final Grammar textmapGrammar = new Grammar();
	private static final Grammar mapInfoGrammar = new Grammar();
	
	static {
		//Defines the Grammar for TEXTMAP
		textmapGrammar.addLexerIgnoreRule("WhiteSpace", "[\t\r\n \\x00]+");
		textmapGrammar.addLexerIgnoreRule("SingleLineComment", "//[^\n]*\n");
		textmapGrammar.addLexerIgnoreRule("MultiLineComment", "/\\*[^\\*]*(\\*[^/][^\\*]*)*\\*/");

		textmapGrammar.addParserRule("ExpressionList", "Expression ExpressionList | /-");
		textmapGrammar.addParserRule("Expression", "Block | Assignment");
		textmapGrammar.addParserRule("Block", "Identifier OpenBracket AssignmentList CloseBracket");
		textmapGrammar.addParserRule("AssignmentList", "Assignment AssignmentList | /-");
		textmapGrammar.addParserRule("Assignment", "Identifier Equals Value Semicolon");
		textmapGrammar.addParserRule("Value", "Integer | Float | QuotedString | Boolean");
		
		textmapGrammar.addLexerRule("OpenBracket", "\\{");
		textmapGrammar.addLexerRule("CloseBracket", "\\}");
		textmapGrammar.addLexerRule("Equals", "=");
		textmapGrammar.addLexerRule("Semicolon", ";");
		textmapGrammar.addLexerRule("Boolean", "true|false");
		textmapGrammar.addLexerRule("Identifier", "[A-Za-z_]+[A-Za-z0-9_]*");
		textmapGrammar.addLexerRule("Integer", "([+-]?[1-9]+[0-9]*)|(0[0-9]*)|(0x[0-9A-Fa-f]+)");
		textmapGrammar.addLexerRule("Float", "[+-]?[0-9]+\\.[0-9]*([eE][+-]?[0-9]+)?");
		textmapGrammar.addLexerRule("QuotedString", "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
		
		//Defines the Grammar for MapInfo
		mapInfoGrammar.addLexerIgnoreRule("WhiteSpace", "[\t\r\n \\x00]+");
		mapInfoGrammar.addLexerIgnoreRule("SingleLineComment", "//[^\n]*\n");
		mapInfoGrammar.addLexerIgnoreRule("MultiLineComment", "/\\*[^\\*]*(\\*[^/][^\\*]*)*\\*/");

		mapInfoGrammar.addParserRule("MapInfo", "Map MapCode QuotedString OpenBrace PropertyList CloseBrace");
		mapInfoGrammar.addParserRule("MapCode", "Identifier | Code");
		mapInfoGrammar.addParserRule("PropertyList", "Property PropertyList | /-");
		mapInfoGrammar.addParserRule("Property", "Identifier Equals ValueList | Identifier");
		mapInfoGrammar.addParserRule("ValueList", "Value Comma ValueList | Value");
		mapInfoGrammar.addParserRule("Value", "QuotedString | Integer | Float | MapCode");

		mapInfoGrammar.addLexerRule("OpenBrace", "\\{");
		mapInfoGrammar.addLexerRule("CloseBrace", "\\}");
		mapInfoGrammar.addLexerRule("Equals", "=");
		mapInfoGrammar.addLexerRule("Comma", ",");
		mapInfoGrammar.addLexerRule("Map", "[Mm][Aa][Pp]");
		mapInfoGrammar.addLexerRule("Float", "[0-9]+\\.[0-9]+");
		mapInfoGrammar.addLexerRule("Integer", "([+-]?[1-9]+[0-9]*)|[0]");
		mapInfoGrammar.addLexerRule("QuotedString", "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
		mapInfoGrammar.addLexerRule("Identifier", "[A-Za-z_]+[A-Za-z0-9_]*");
		mapInfoGrammar.addLexerRule("Code", "[A-Za-z0-9_]*");
	}
	

	public final String mapCode;
	public final String mapName;
	
	private final HashMap<String, ArrayList<Block>> blocks = new HashMap<>();
	private String namespace;
	

	public UniversalDoomMap(WadFile wad) {
		
		byte[] mapData = wad.getLump("TextMap");
		byte[] mapInfo = wad.getLump("MapInfo");
		
		if(mapData == null || mapInfo == null)
			throw new IllegalArgumentException("The specified wad does not contain UDMF map data");
		
		try {
			
			//Parses the TEXTMAP lump
			ParserToken token = textmapGrammar.compileParser(new ByteStream(mapData)).parse("ExpressionList");
			
			if(token == null)
				throw new RuntimeException("Error parsing TEXTMAP lump");
			
			//Visits the TEXTMAP lump to load all of the Blocks
			token.accept(new ExpressionVisitor(this));
			
			//Parses the MAPINFO lump
			token = mapInfoGrammar.compileParser(new ByteStream(mapInfo)).parse("MapInfo");
			
			if(token == null)
				throw new RuntimeException("Error parsing MAPINFO lump");
			
			//We know where the MapCode and MapName are in the AST, so no use visiting it, just retrieve it the old fashioned way.
			mapCode = token.getChild(1).value;
			String quotedName = token.getChild(2).value;
			mapName = quotedName.substring(1, quotedName.length()-1); //Get rid of the quotes around the quoted map name
		} catch (ParserException e) {
			
			throw new IllegalArgumentException("Error parsing TEXTMAP lump", e);
		}		
	}
	
	/**
	 * Adds a block of data to this map, called from the ExpressionVisitor when visiting the AST of the TEXTMAP lump.
	 * @param block The block to be added.
	 */
	protected void addBlock(Block block) {
		
		ArrayList<Block> blockList = blocks.get(block.blockType);
		if(blockList == null) {
			//If we don't have a list of blocks for this block type, create one
			blockList = new ArrayList<>();
			blocks.put(block.blockType, blockList);
		}
		
		//Add the block to the blockList for the specified type
		blockList.add(block);
	}
	
	/**
	 * Retrieves the namespace defined in the TEXTMAP lump.
	 * @return The namespace used for this UDMF map.
	 */
	public String getNamespace() {
		
		return namespace;
	}
	
	/**
	 * Sets the namespace to what is defined in the TEXTMAP lump, this is called from the ExpressionVistior when 
	 * visiting the AST of the TEXTMAP lump.
	 * @param namespace The namespace in the TEXTMAP lump.
	 */
	protected void setNamespace(String namespace) {
		
		this.namespace = namespace;
	}
	
	/**
	 * Retrieves the list of blocks of the specified type defined in this maps' TEXTMAP lump.
	 * Modifications made to the returned list will be reflected in the map.
	 * @param type The type of block to be returned.
	 * @return The list of blocks of the specified type, or null if no such blocks exist.
	 */
	public ArrayList<Block> getBlocks(String type){
		
		return blocks.get(type.toLowerCase());
	}
}
