package me.michael4797.parse;

import java.util.ArrayList;

/**
 * A type of token that acts as a branch node in the AST, i.e. it has children.
 */
public class ParserToken extends Token {

	private final ArrayList<Token> children;
	
	
	public ParserToken(String name, ArrayList<Token> children) {
		
		super(name, "");
		this.children = children;
	}
	
	
	@Override
	public int getChildCount() {
		
		return children.size();
	}
	
	
	@Override
	public Token getChild(int i) {
		
		return children.get(i);
	}
}
