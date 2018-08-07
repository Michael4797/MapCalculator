package me.michael4797.udmf;

/**
 * Used in Blocks to enforce type safety when storing and retrieving properties from UDMF maps.
 */
public class Value {

	
	public boolean asBoolean() {
		
		throw new ClassCastException("Tried to convert " + getClass().getSimpleName() + " to Boolean type");
	}

	
	public int asInteger() {
		
		throw new ClassCastException("Tried to convert " + getClass().getSimpleName() + " to Integer type");
	}

	
	public double asFloat() {
		
		throw new ClassCastException("Tried to convert " + getClass().getSimpleName() + " to Float type");
	}

	
	public String asString() {
		
		throw new ClassCastException("Tried to convert " + getClass().getSimpleName() + " to String type");
	}
}
