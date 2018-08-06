package me.michael4797.udmf;

public class SectorBlock extends Block{

	
	public SectorBlock() {
		
		super("sector");

		putValue("heightfloor", new IntegerValue(0));
		putValue("heightceiling", new IntegerValue(0));
		putValue("lightlevel", new IntegerValue(160));
		putValue("special", new IntegerValue(0));
		putValue("id", new IntegerValue(0));
		
		putValue("comment", new StringValue(""));
	}
}
