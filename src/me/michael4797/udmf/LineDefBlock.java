package me.michael4797.udmf;

public class LineDefBlock extends Block{

	
	public LineDefBlock() {
		
		super("linedef");

		putValue("blocking", new BooleanValue(false));
		putValue("blockmonsters", new BooleanValue(false));
		putValue("blockplayers", new BooleanValue(false));
		putValue("blockeverything", new BooleanValue(false));
		putValue("twosided", new BooleanValue(false));
		putValue("dontpegtop", new BooleanValue(false));
		putValue("dontpegbottom", new BooleanValue(false));
		putValue("secret", new BooleanValue(false));
		putValue("blocksound", new BooleanValue(false));
		putValue("dontdraw", new BooleanValue(false));
		putValue("mapped", new BooleanValue(false));
		putValue("passuse", new BooleanValue(false));
		putValue("translucent", new BooleanValue(false));
		putValue("jumpover", new BooleanValue(false));
		putValue("blockfloaters", new BooleanValue(false));
		putValue("playercross", new BooleanValue(false));
		putValue("playeruse", new BooleanValue(false));
		putValue("monstercross", new BooleanValue(false));
		putValue("monsteruse", new BooleanValue(false));
		putValue("impact", new BooleanValue(false));
		putValue("playerpush", new BooleanValue(false));
		putValue("monsterpush", new BooleanValue(false));
		putValue("missilecross", new BooleanValue(false));
		putValue("repeatspecial", new BooleanValue(false));

		putValue("id", new IntegerValue(-1));
		putValue("special", new IntegerValue(0));
		putValue("arg0", new IntegerValue(0));
		putValue("arg1", new IntegerValue(0));
		putValue("arg2", new IntegerValue(0));
		putValue("arg3", new IntegerValue(0));
		putValue("arg4", new IntegerValue(0));
		putValue("sideback", new IntegerValue(-1));

		putValue("comment", new StringValue(""));
	}
}
