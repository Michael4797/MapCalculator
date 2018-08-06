package me.michael4797.udmf;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Visits blocks in the UDMF TEXTMAP lump to load the properties for Block objects.
 */
public class BlockVisitor implements TokenVisitor<Void>{
	
	protected static final ValueVisitor visitor = new ValueVisitor();
	private final Block block;
	
	/**
	 * Visits a Block Token to load the properties of the specified Block object.
	 * @param block The Block to load.
	 */
	public BlockVisitor(Block block) {
		
		this.block = block;
	}
	
	
	@Override
	public Void visit(Token token) {
		
		throw new RuntimeException("Attempted to visit invalid token type " + token);
	}
	
	
	public Void visitAssignmentList(Token token) {
		
		for(int i = 0; i < token.getChildCount(); ++i)
			token.getChild(i).accept(this); //Visits each assignment in the block
		
		return null;
	}
	
	
	public Void visitAssignment(Token token) {
		
		String identifier = token.getChild(0).value; //The name of the property being defined
		Value value = token.getChild(2).accept(visitor); //The value of the property
		
		block.putValue(identifier, value); //Load the property
		return null;
	}
}
