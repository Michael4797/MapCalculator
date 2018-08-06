package me.michael4797.udmf;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Visits the expressions in the TEXTMAP lump to initialize the namespace and blocks for a UDMF map.
 */
public class ExpressionVisitor implements TokenVisitor<Void>{

	private final UniversalDoomMap map;
	
	
	public ExpressionVisitor(UniversalDoomMap map) {
		
		this.map = map;
	}
	
	
	@Override
	public Void visit(Token token) {
		
		throw new RuntimeException("Attempted to visit invalid token type " + token);
	}

	
	//ExpressionList is the root node of the TEXTMAP Grammar
	public Void visitExpressionList(Token token) {
		
		for(int i = 0; i < token.getChildCount(); ++i)
			token.getChild(i).accept(this); //Visit each expression
		
		return null;
	}

	
	public Void visitExpression(Token token) {

		token.getChild(0).accept(this);
		return null;		
	}

	
	public Void visitAssignment(Token token) {

		String identifier = token.getChild(0).value;
		Value value = token.getChild(2).accept(BlockVisitor.visitor);
		
		if(identifier.equalsIgnoreCase("namespace")) //If the identifier being assigned is 'namespace' then set the namespace of our UDDMF map
			map.setNamespace(value.asString());
		else
			throw new RuntimeException("Unexpected assignment expression found " + token);
		
		return null;		
	}
	
	
	public Void visitBlock(Token token) {

		//We found a block, we should load the data and add it to our UDMF map
		String blockType = token.getChild(0).value;
		Block block;
		//A more descriptive grammar could have handled this part for us,
		//but this works all the same.
		//We need to know which type of block we're initializing so that we can setup the
		//default values for that type.
		if(blockType.equalsIgnoreCase("thing"))
			block = new ThingBlock();
		else if(blockType.equalsIgnoreCase("vertex"))
			block = new VertexBlock();
		else if(blockType.equalsIgnoreCase("linedef"))
			block = new LineDefBlock();
		else if(blockType.equalsIgnoreCase("sidedef"))
			block = new SideDefBlock();
		else if(blockType.equalsIgnoreCase("sector"))
			block = new SectorBlock();
		else
			block = new Block(token.getChild(0).value);
		
		token.getChild(2).accept(new BlockVisitor(block)); //Visit the block to load its properties.
		map.addBlock(block);
		return null;		
	}
}
