package me.michael4797.parse;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains matching information used by the Parser to construct ParserTokens.
 * A ParserRule is comprised of a disjunction of TokenPatterns.
 */
public class ParserRule implements Iterable<TokenPattern>{

	public final ArrayList<TokenPattern> patterns = new ArrayList<TokenPattern>();
	
	/**
	 * Constructs a ParserRule.
	 * @param rule The rule, represented as a disjunction of series of one or more Tokens.
	 * @throws ParserException If the ParserRule is of an invalid format.
	 */
	public ParserRule(String rule) throws ParserException {
		
		String[] patterns = rule.split("\\|");
		for(String pattern: patterns)	
			this.patterns.add(new TokenPattern(pattern));
	}


	@Override
	public Iterator<TokenPattern> iterator() {

		return patterns.iterator();
	}
}
