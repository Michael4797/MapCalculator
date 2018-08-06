package me.michael4797.parse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * A single unit of input parsed by a Lexer or interpreted by a Parser.
 */
public class Token implements Iterable<LexerToken>{

	public final String name;
	public final String value;
	
	/**
	 * Constructs a token to be used by a Lexer or a Parser
	 * @param name The name of the token, as specified in the Grammar.
	 * @param value The textual representation of this token, or null if this token is a branch.
	 */
	public Token(String name, String value) {
		
		this.name = name;
		this.value = value;
	}


	/**
	 * Gets the number of children.
	 * @return The number of children, or -1 if this Token is a leaf.
	 */
	public int getChildCount() {
		
		return -1;
	}
	
	/**
	 * Gets the specified child.
	 * @param i The child to retrieve.
	 * @return The specified child, or null if this Token is a leaf.
	 */
	public Token getChild(int i) {
		
		return null;
	}
	
	
	/**
	 * Accepts the specified visitor and calls the appropriate visit function for this Token.
	 * @param visitor The visitor attempting to visit this Token.
	 * @return The result of visiting this token with the specified visitor.
	 */
	@SuppressWarnings("unchecked")
	public <T> T accept(TokenVisitor<T> visitor) {
		
		try {
			Method visitMethod = visitor.getClass().getMethod("visit" + name, Token.class);
			if(visitMethod.getReturnType() == visitor.getClass().getMethod("visit", Token.class).getReturnType() && visitMethod.getExceptionTypes().length == 0)
				return (T) visitMethod.invoke(visitor, this);
		}catch(NoSuchMethodException e) {}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
			throw new RuntimeException("Error accepting visitor", e);
		}
		
		return visitor.visit(this);
	}
	
	
	@Override
	public String toString() {
		
		return name + ": " + value;
	}


	@Override
	public Iterator<LexerToken> iterator() {

		return new TokenIterator(this);
	}
}
