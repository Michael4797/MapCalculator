package me.michael4797.parse;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Caches information regarding previous parsing attempts in the current Parser operation.
 */
public class ParserContext {

	private final HashMap<LexerToken, HashSet<String>> failedTokens = new HashMap<>();
	private final HashMap<LexerToken, HashMap<String, ParserToken>> succeededTokens = new HashMap<>();
	

	/**
	 * Caches a failed parsing attempt to parse the specified token at the specified starting token.
	 * @param token The token that failed to parse.
	 * @param startingPoint The position at which it failed.
	 */
	protected void failParse(String token, LexerToken startingPoint) {
		
		HashSet<String> failed = failedTokens.get(startingPoint);
		if(failed == null) {
			
			failed = new HashSet<>();
			failedTokens.put(startingPoint, failed);
		}
		
		failed.add(token);
	}
	
	/**
	 * Caches a successful parsing attempt at parsing the specified token at the specified starting token.
	 * @param token The token that successfully parsed.
	 * @param startingPoint The position at which it was parsed.
	 */
	protected void succeedParse(ParserToken token, LexerToken startingPoint) {
		
		HashMap<String, ParserToken> tokens = succeededTokens.get(startingPoint);
		if(tokens == null) {
			
			tokens = new HashMap<>();
			succeededTokens.put(startingPoint, tokens);
		}
		
		tokens.put(token.name, token);
	}
	
	/**
	 * Checks if the specified token has previously failed at being parsed at the specified location.
	 * @param token The token to check.
	 * @param startingPoint The position to check.
	 * @return True if the token has previously failed to be parsed at the specified location.
	 */
	protected boolean hasFailedParse(String token,  LexerToken startingPoint) {
		
		HashSet<String> failed = failedTokens.get(startingPoint);
		return failed != null && failed.contains(token);
	}
	
	/**
	 * Checks if the specified token has previously succeeded at being parsed at the specified location.
	 * @param token The token to check.
	 * @param startingPoint The position to check.
	 * @return The cached token that was parsed at the specified location, or null if it doesn't exist.
	 */
	protected ParserToken hasSucceededParse(String token, LexerToken startingPoint) {
		
		HashMap<String, ParserToken> succeeded = succeededTokens.get(startingPoint);
		return succeeded != null ? succeeded.get(token) : null;
	}
}
