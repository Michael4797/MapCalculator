package me.michael4797.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains matching information used by the Lexer when parsing LexerTokens.
 */
public class LexerRule {

	public final String token;
	public final boolean ignore;
	private final Matcher matcher;
	private boolean matches, valid;
	private int matchLength;

	/**
	 * Constructs a new rule for construction LexerTokens.
	 * @param token The name of the Token.
	 * @param rule The pattern of the Token in regex.
	 */
	public LexerRule(String token, String rule) {
		
		this.token = token;
		ignore = false;
		matcher = Pattern.compile(rule).matcher("");
	}
	
	/**
	 * Constructs a new rule for construction LexerTokens.
	 * @param token The name of the Token.
	 * @param rule The pattern of the Token in regex.
	 * @param ignore Whether or not tokens lexed using this rule should be thrown out.
	 */
	public LexerRule(String token, String rule, boolean ignore) {

		this.token = token;
		this.ignore = ignore;
		matcher = Pattern.compile(rule).matcher("");
	}
	
	/**
	 * Attempts to match this token with the specified input.
	 * @param token The input to match.
	 * @param offset The offset into the input to begin matching.
	 */
	public void setInput(StringBuffer token, int offset) {
		
		matcher.reset(token);
		matcher.region(offset, token.length());
		matches = matcher.lookingAt();
		
		if(matches)
			matchLength = matcher.end() - matcher.start();
		
		valid = matcher.hitEnd();
	}
	
	/**
	 * Checks if this LexerRule can still be matched.
	 * @return False is this LexerRule cannot be matched.
	 */
	public boolean valid() {
		
		return valid;
	}
	
	/**
	 * Checks if the rule is matched on the given input.
	 * @return True if the rule is matched.
	 */
	public boolean matches() {
		
		return matches;
	}
	
	/**
	 * Retrieves the length of the matched input.
	 * @return The length.
	 */
	public int matchLength() {
		
		return matchLength;
	}
}
