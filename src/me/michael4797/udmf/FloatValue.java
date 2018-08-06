package me.michael4797.udmf;

public class FloatValue extends Value{

	private final double value;
	
	
	public FloatValue(double value) {
		
		this.value = value;
	}
	
	
	@Override
	public double asFloat() {
		
		return value;
	}
}
