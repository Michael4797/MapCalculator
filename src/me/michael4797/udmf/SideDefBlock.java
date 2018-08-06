package me.michael4797.udmf;

public class SideDefBlock extends Block{

	
	public SideDefBlock() {
		
		super("sidedef");

		putValue("offsetx", new IntegerValue(0));
		putValue("offsety", new IntegerValue(0));

		putValue("texturetop", new StringValue("-"));
		putValue("texturebottom", new StringValue("-"));
		putValue("texturemiddle", new StringValue("-"));
		putValue("comment", new StringValue(""));
	}
}
