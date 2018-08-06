package me.michael4797.udmf;

public class ThingBlock extends Block{

	
	public ThingBlock(){
		
		super("thing");

		putValue("skill1", new BooleanValue(false));
		putValue("skill2", new BooleanValue(false));
		putValue("skill3", new BooleanValue(false));
		putValue("skill4", new BooleanValue(false));
		putValue("skill5", new BooleanValue(false));
		putValue("ambush", new BooleanValue(false));
		putValue("single", new BooleanValue(false));
		putValue("dm", new BooleanValue(false));
		putValue("coop", new BooleanValue(false));
		putValue("friend", new BooleanValue(false));
		putValue("dormant", new BooleanValue(false));
		putValue("class1", new BooleanValue(false));
		putValue("class2", new BooleanValue(false));
		putValue("class3", new BooleanValue(false));
		putValue("standing", new BooleanValue(false));
		putValue("strifeally", new BooleanValue(false));
		putValue("translucent", new BooleanValue(false));
		putValue("invisible", new BooleanValue(false));
		
		putValue("id", new IntegerValue(0));
		putValue("angle", new IntegerValue(0));
		putValue("special", new IntegerValue(0));
		putValue("arg0", new IntegerValue(0));
		putValue("arg1", new IntegerValue(0));
		putValue("arg2", new IntegerValue(0));
		putValue("arg3", new IntegerValue(0));
		putValue("arg4", new IntegerValue(0));

		putValue("height", new FloatValue(0.0));

		putValue("comment", new StringValue(""));
	}
}
