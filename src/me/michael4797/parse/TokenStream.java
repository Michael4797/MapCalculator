package me.michael4797.parse;

/**
 * An interface for linking a Parser and a Lexer.
 */
public interface TokenStream {

	/**
	 * Retrieves the next LexerToken from the source.
	 * @return The next LexerToken.
	 * @throws ParserException If no LexerToken can be parsed.
	 */
	public LexerToken nextToken() throws ParserException;
}
