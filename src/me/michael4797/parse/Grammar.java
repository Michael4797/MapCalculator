package me.michael4797.parse;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * A class used to construct the rules by which a resulting Lexer and Parser will behave.
 */
public class Grammar {

	/**
	 * The textual representation of the empty string, interpreted by the parser.
	 */
	public static final String lambda = "/-";
	
	private final LinkedList<LexerRule> lexerRules = new LinkedList<>();
	private final LinkedHashMap<String, ParserRule> parserRules = new LinkedHashMap<>();
	
	/**
	 * Adds a rule to be used when lexing tokens from the input.
	 * @param tokenName The name of the resulting token.
	 * @param rule The pattern of the token, in regex.
	 */
	public void addLexerRule(String tokenName, String rule) {
		
		lexerRules.add(new LexerRule(tokenName, rule));
	}
	
	/**
	 * Adds a rule to be used when lexing tokens from the input, whenever one of the
	 * specified tokens is lexed, it will be thrown out.
	 * @param tokenName The name of the resulting token.
	 * @param rule The pattern of the token, in regex.
	 */
	public void addLexerIgnoreRule(String tokenName, String rule) {

		lexerRules.add(new LexerRule(tokenName, rule, true));
	}
	
	/**
	 * Adds a rule to be used when parsing tokens from the lexer.
	 * @param tokenName The name of the resulting token.
	 * @param rule The pattern of the token, as a disjunction of series of one or more tokens.
	 */
	public void addParserRule(String tokenName, String rule) {
		
		try {
			
			if(parserRules.put(tokenName, new ParserRule(rule)) != null)
				throw new RuntimeException("Duplicate rule for token " + tokenName);
		} catch (ParserException e) {
			
			throw new RuntimeException("Error adding parser rule", e);
		}
	}

	/**
	 * Compiles a lexer with the predetermined rules used to lex the specified InputStream.
	 * @param source The InputStream to lex.
	 * @return The newly constructed Lexer.
	 */
	public Lexer compileLexer(InputStream source) {
		
		return new Lexer(source, lexerRules);
	}

	/**
	 * Compiles a parser with the predetermined rules used to parse the specified TokenStream.
	 * @param source The TokenStream to parse.
	 * @return The newly constructed Parser.
	 */
	public Parser compileParser(TokenStream source) {
		
		return new Parser(source, parserRules);
	}

	/**
	 * Compiles a parser and a lexer with the predetermined rules used to parse the specified InputStream.
	 * @param source The InputStream to parse.
	 * @return The newly constructed Parser.
	 */
	public Parser compileParser(InputStream source) {
		
		return new Parser(new Lexer(source, lexerRules), parserRules);
	}
}
