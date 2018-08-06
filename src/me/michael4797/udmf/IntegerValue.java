package me.michael4797.udmf;

public class IntegerValue extends Value{

	private final int value;
	
	
	public IntegerValue(int value) {
		
		this.value = value;
	}
	
	
	@Override
	public int asInteger() {
		
		return value;
	}
}
