package me.michael4797.udmf;

import java.util.HashMap;

/**
 * Contains a mapping of properties for a block type in a TEXTMAP lump.
 */
public class Block {

	private final HashMap<String, Value> values = new HashMap<>();
	public final String blockType;
	
	/**
	 * Creates a Block with no default properties and the specified type.
	 * @param type The type of the block.
	 */
	public Block(String type) {
		
		this.blockType = type.toLowerCase();
	}
	
	/**
	 * Used to set a property in this block as a key-value pair.
	 * @param name The name of the property.
	 * @param value The value of the property.
	 */
	protected void putValue(String name, Value value) {
		
		values.put(name.toLowerCase(), value);
	}
	
	/**
	 * Retrieves the specified property and interprets it as a boolean.
	 * Throws an Exception if the specified property is not a boolean type.
	 * @param name The name of the property to read.
	 * @return The value of the property as a boolean.
	 */
	public boolean getBoolean(String name) {
		
		Value value = values.get(name.toLowerCase());
		if(value == null)
			throw new IllegalArgumentException("No such value '" + name + "'");
		
		return value.asBoolean();
	}
	
	/**
	 * Retrieves the specified property and interprets it as an integer.
	 * Throws an Exception if the specified property is not an integer type.
	 * @param name The name of the property to read.
	 * @return The value of the property as an integer.
	 */
	public int getInteger(String name) {
		
		Value value = values.get(name.toLowerCase());
		if(value == null)
			throw new IllegalArgumentException("No such value '" + name + "'");
		
		return value.asInteger();
	}
	
	/**
	 * Retrieves the specified property and interprets it as a float.
	 * Throws an Exception if the specified property is not a float type.
	 * @param name The name of the property to read.
	 * @return The value of the property as a float.
	 */
	public double getFloat(String name) {
		
		Value value = values.get(name.toLowerCase());
		if(value == null)
			throw new IllegalArgumentException("No such value '" + name + "'");
		
		return value.asFloat();
	}
	
	/**
	 * Retrieves the specified property and interprets it as a String.
	 * Throws an Exception if the specified property is not a String type.
	 * @param name The name of the property to read.
	 * @return The value of the property as a String.
	 */
	public String getString(String name) {
		
		Value value = values.get(name.toLowerCase());
		if(value == null)
			throw new IllegalArgumentException("No such value '" + name + "'");
		
		return value.asString();
	}
}
