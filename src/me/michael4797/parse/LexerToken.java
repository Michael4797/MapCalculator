package me.michael4797.parse;

/**
 * A type of token that acts as a leaf to the AST. They do not have children.
 */
public class LexerToken extends Token {

	public LexerToken(String name, String value) {
		
		super(name, value);
	}

}
