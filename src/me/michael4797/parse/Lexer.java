package me.michael4797.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * The base class used to construct LexerTokens from an InputStream based
 * on a list of rules.
 */
public class Lexer implements TokenStream{

	public final LinkedList<LexerRule> rules;
	private final InputStream in;
	private final byte[] data;
	
	private int index;
	
	private final StringBuffer current = new StringBuffer();
	
	/**
	 * Creates a Lexer that is capable of constructing the defined LexerRules into LexerTokens
	 * by reading from the specified source.
	 * @param source The InputStream to be lexed.
	 * @param rules The rules defining the Tokens to be lexed.
	 */
	public Lexer(InputStream source, LinkedList<LexerRule> rules) {
		
		this.rules = rules;
		in = source;
		data = new byte[8192];
	}


	/**
	 * Matches a LexerToken from the source InputStream based on the specified LexerRules.
	 * The Token that is returned is the longest token that could be parsed from the input.
	 * If a tie exists, that is multiple tokens of the same length could be parsed, the one that
	 * was defined first is returned.
	 */
	@Override
	public LexerToken nextToken() throws ParserException {
		
		final LexerMatch match = new LexerMatch();

		if(current.length() - index <= 0)
			readBlock(); //If we've matched everything in our buffer, read more data from the source

		do{
			
			match.length = 0;
			match.rule = null;
			
			findBestMatch(match); //Match the next token
			
			if(match.rule == null)
				throw new ParserException("Unable to match lexer token for input " + current.substring(index));
			
			index += match.length;
		}while(match.rule.ignore); //If the rule should be discarded, skip it and parse the next token.
		
		return new LexerToken(match.rule.token, current.substring(index - match.length, index)); //Construct the token from the matched LexerRule
	}
	
	
	private void findBestMatch(LexerMatch match) throws ParserException {
	
		boolean firstRun = true;
		boolean noPossible = true;
		
		while(true) {
			
			for(LexerRule rule: rules) {
				
				if(!firstRun && !rule.valid()) //If we've already tried to match this rule once, and it's not valid, skip it
					continue;
				
				rule.setInput(current, index); //Do some pattern matching on the rule with the current input
				
				if(rule.matches() && rule.matchLength() > match.length) { //If the rule matches, and it's longer than our best, update the best
	
					match.rule = rule;
					match.length = rule.matchLength();
				}
				
				if(rule.valid()) //If at least one rule is valid, keep going
					noPossible = false;
			}
			
			if(noPossible) //No rules match anymore, we've gone too far, everyone run
				break;
			
			noPossible = true;
			firstRun = false;
			
			try {
				
				readBlock(); //Some rules are still valid, so get more input until we wear them down
			}catch(ParserEndOfStreamException e) {
				
				//If we have no match and no input, we have a problem, so yell really loudly.
				if(match.rule == null)
					throw e;

				//If we have no more input, and we have a possible match, just return the match.
				break;
			}
		}
	}
	
	/**
	 * Reads a chunk of input from the source InputStream and adds it to our buffer.
	 * @throws ParserException If there is an error reading from the InputStream, or the end of the input has been reached.
	 */
	private void readBlock() throws ParserException {
		
		if(index >= current.length())
			current.setLength(0);
		else
			current.delete(0, index);
		
		index = 0;
		try {
			
			int len = in.read(data, 0, data.length);
			if(len == -1)
				throw new ParserEndOfStreamException("Unable to read from source, end of stream.");
			
			for(int i = 0; i < len; ++i) 
				current.append((char)(data[i]&255));
		} catch (IOException e) {
			
			throw new ParserException("Error reading from source", e);
		}
	}
	
	/**
	 * Helper class containing information used to match the best LexerRule
	 */
	private static class LexerMatch{
		
		private LexerRule rule = null;
		private int length = 0;
	}
}
