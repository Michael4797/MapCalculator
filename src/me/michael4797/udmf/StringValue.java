package me.michael4797.udmf;

public class StringValue extends Value{

	private final String value;
	
	
	public StringValue(String value) {
		
		this.value = value;
	}
	
	
	@Override
	public String asString() {
		
		return value;
	}
}
