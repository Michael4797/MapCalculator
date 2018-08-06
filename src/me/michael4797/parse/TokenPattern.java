package me.michael4797.parse;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains matching information used by the Parser to construct ParserTokens
 * A TokenPattern is comprised of a series of one or more Tokens that must be matched in order.
 */
public class TokenPattern implements Iterable<String>{

	public final ArrayList<String> pattern = new ArrayList<>();
	
	/**
	 * Constructs a TokenPatter from the specified String.
	 * @param pattern The pattern, represented as a series of Tokens.
	 * @throws ParserException If the pattern is invalid.
	 */
	public TokenPattern(String pattern) throws ParserException {

		String[] tokens = pattern.split("[\t\r\n ]+");
		boolean empty = false;
		for(String token: tokens) {
			
			if(token.equals(Grammar.lambda)) {
			
				empty = true;
				this.pattern.add("");
			}
			else if(!token.isEmpty())
				this.pattern.add(token);
		}
		
		if(empty && this.pattern.size() > 1)
			throw new ParserException("Invalid pattern: Patterns containing lambda must not contain any other tokens");
	}


	@Override
	public Iterator<String> iterator() {

		return pattern.iterator();
	}
}
