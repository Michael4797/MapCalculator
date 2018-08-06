package me.michael4797.udmf;

public class BooleanValue extends Value{

	private final boolean value;
	
	
	public BooleanValue(boolean value) {
		
		this.value = value;
	}
	
	
	@Override
	public boolean asBoolean() {
		
		return value;
	}
}
