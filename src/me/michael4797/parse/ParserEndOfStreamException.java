package me.michael4797.parse;

/**
 * Exception thrown when a Token is expected from the Lexer but no more input exists.
 */
public class ParserEndOfStreamException extends ParserException{

	private static final long serialVersionUID = 1L;
	

	public ParserEndOfStreamException(String message) {
		
		super(message);
	}
	

	public ParserEndOfStreamException(String message, Throwable cause) {
		
		super(message, cause);
	}
}
