package me.michael4797.parse;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * The base class used to construct ParserTokens from a TokenStream based
 * on a list of rules.
 */
public class Parser {
	
	private final TokenStream in;
	protected final LinkedHashMap<String, ParserRule> rules;
	
	private final ArrayDeque<LexerToken> backlog = new ArrayDeque<>();
	
	/**
	 * Creates a new parser that reads LexerTokens from the specified TokenStream and constructs ParserTokens using
	 * the specified ParserRules.
	 * @param source The source of LexerTokens.
	 * @param parserRules The ParserRules by which Tokens are constructed.
	 */
	public Parser(TokenStream source, LinkedHashMap<String, ParserRule> parserRules) {
		
		in = source;
		rules = parserRules;
	}
	
	/**
	 * Attempts to parse the specified token at the current location.
	 * @param token The name of the token to parse.
	 * @return The parsed token, or null if it could not be parsed.
	 * @throws ParserException If an error is encountered with one of the ParserRules.
	 */
	public ParserToken parse(String token) throws ParserException {
		
		return parse(token, new ParserContext());
	}
	
	/**
	 * Attempts to parse the specified token at the current location.
	 * @param token The name of the token to parse.
	 * @param context The cached parser information, used to accelerate the parsing operation.
	 * @return The parsed token, or null if it could not be parsed.
	 * @throws ParserException If an error is encountered with one of the ParserRules.
	 */
	protected ParserToken parse(String token, ParserContext context) throws ParserException {
		
		ParserToken parsed = null;
		String next = null;
		LexerToken nextToken = null;
		Token matchedToken = null;

		boolean previouslyFailed = false;
		ParserToken previouslySucceeded = null;
		
		//We use a Stack of ParserFrames instead of calling this method recursively
		//Since ParserRules can be complex and recursive, calling this method recursively
		//is very likely to cause a stack overflow.
		ArrayDeque<ParserFrame> frames = new ArrayDeque<>();
		//Create the first frame used for parsing our specified token.
		ParserFrame frame = new ParserFrame(token, rules);
		
		while(true) {
			
			//Get the next token in the current pattern we're trying to match.
			next = frame.next();
			
			//If we have some tokens on the backlog, then it's possible we've tried to parse this token already.
			if(backlog.size() > 0 && rules.containsKey(next)) {
			
				//So check the context to see if we've already tried and cached the result.
				previouslyFailed = context.hasFailedParse(next, backlog.peek());
				previouslySucceeded = context.hasSucceededParse(next, backlog.peek());
			}
			else {
				
				previouslyFailed = false;
				previouslySucceeded = null;
			}
			
			//If we haven't cached the result, and the next token is a ParserRule, recurse by adding a new frame to the stack.
			if(!next.isEmpty() && !previouslyFailed && previouslySucceeded == null && rules.containsKey(next)) {
				
				frames.push(frame);
				frame = new ParserFrame(next, rules);
			}
			else {
				
				//If we cached the outcome of this parse operation, or we're looking for an empty string, don't read the next token.
				if(previouslyFailed || previouslySucceeded != null || next.isEmpty()) {
				
					nextToken = null;
				}
				else {
				
					try {
						
						nextToken = nextToken();
					}catch(ParserEndOfStreamException e) {
						
						nextToken = null;
					}
				}
				
				//If we succeeded in matching the next token
				if(!previouslyFailed && (next.isEmpty() || previouslySucceeded != null || (nextToken != null && nextToken.name.equals(next)))) {
					
					//If we succeeded because we cached the result
					if(previouslySucceeded != null) {
						
						//Remove all the matched tokens from the backlog
						matchedToken = previouslySucceeded;
						for(Iterator<LexerToken> iterator = previouslySucceeded.iterator(); iterator.hasNext(); iterator.next())
							nextToken();

						//Collapse any tokens that only have 1 child
						while(matchedToken.getChildCount() == 1)
							matchedToken = matchedToken.getChild(0);
					}
					else //If we succeeded without the cache, that means the next token we read from the Lexer is the one that matched.
						matchedToken = nextToken;
					
					//Add the matched token to the token chain in our frame
					//If adding this token completes the pattern, then pop the
					//last frame from the stack and match the newly parsed token
					while((parsed = frame.matched(matchedToken)) != null) {
				
						if(frames.isEmpty()) //If we just matched the last frame, we're done
							return parsed;
						
						frame = frames.pop();
						
						//If we didn't parse an empty string, then we should cache the result in case we need it later.
						if(!next.isEmpty()) {
						
							LexerToken origin = parsed.iterator().next();
							context.succeedParse(parsed, origin);
						}
						
						matchedToken = parsed;
					}
				}
				//The next token didn't match out pattern
				else {
					//Fail the current pattern in the Frame and try the next one
					//If there is no next pattern in the frame, then pop a frame
					//from the stack and fail that pattern too.
					while(!frame.fail(nextToken, backlog)) {

						if(frames.isEmpty()) //If we failed the last frame, we've lost all hope
							return null;
						
						//If we put any effort at all into parsing this Token, we should cache the result 
						if(!previouslyFailed && frame.children.size() > 0) {
							
							LexerToken origin = frame.children.get(0).iterator().next();
							context.failParse(frame.token, origin);
						}
						
						frame = frames.pop();
						nextToken = null;
					}
				}
			}
		}
	}
		
	/**
	 * Reads the next token from either the the source TokenStream or the backlog.
	 * @return The next token.
	 * @throws ParserException If there's an error reading the next token.
	 */
	protected LexerToken nextToken() throws ParserException {
		
		if(backlog.isEmpty())
			return in.nextToken();
		else
			return backlog.poll();
	}
	
	
	/**
	 * Helper class used to avoid calling the parse method recursively.
	 * This class contains all the information about a specific step in the parse operation.
	 */
	private static class ParserFrame{
		
		private static final ArrayDeque<LexerToken> tokenStack = new ArrayDeque<>();
		
		private final String token;
		private final ArrayList<Token> children = new ArrayList<>();
		private final ArrayList<TokenPattern> patterns;
		private ArrayList<String> tokens;
		private int patternIndex = 0;
		private int tokenIndex = 0;
		
		
		private ParserFrame(String token, LinkedHashMap<String, ParserRule> rules) throws ParserException {
			
			this.token = token;
			ParserRule rule = rules.get(token);
			if(rule == null)
				throw new ParserException("No rule exists for parsing token " + token);
			
			patterns = rule.patterns;
			
			if(patterns.size() == 0)
				throw new ParserException("Rule for token " + token + " has no valid patterns");
			
			tokens = patterns.get(patternIndex++).pattern;
		}
		
		/**
		 * Gets the next token in the current pattern.
		 * @return The name of the next token.
		 */
		private String next() {
			
			return tokens.get(tokenIndex++);
		}
		
		/**
		 * Adds the matched token to this frame. If the token completes the pattern, returns
		 * the newly parsed token.
		 * @param parsed The newly parsed token.
		 * @return If the current TokenPattern has been completely matched, the completed ParserToken is returned, otherwise, null.
		 */
		private ParserToken matched(Token parsed) {
			
			if(parsed != null) {
				
				if(parsed.name.equals(token))
					for(int i = 0; i < parsed.getChildCount(); ++i) //If the TokenPattern is defined recursively, collapse the tail-end recursion into a list
						children.add(parsed.getChild(i));
				else if(parsed.getChildCount() == 1) //If the Token we just parsed only has one child, collapse that token.
					children.add(parsed.getChild(0));
				else
					children.add(parsed);					
			}
					
			
			if(tokenIndex >= tokens.size())
				return new ParserToken(token, children);
			
			return null;
		}
		
		/**
		 * Fail to parse the current TokenPattern.
		 * @param failPoint The token that broke the pattern.
		 * @param backlog The queue to add the failed token chain to.
		 * @return True if there are more possible TokenPatterns in this Frame, false if it's a lost cause.
		 */
		private boolean fail(LexerToken failPoint, ArrayDeque<LexerToken> backlog) {
			
			//We can only iterate forwards through the children, but we need to add them backwards into our backlog
			//so we use a stack to reverse their order.
			for(Token child: children)
				for(LexerToken lt: child)
					tokenStack.push(lt);
			
			if(failPoint != null)
				backlog.addFirst(failPoint);
			
			while(!tokenStack.isEmpty())
				backlog.addFirst(tokenStack.pop());
			
			tokenIndex = 0;
			children.clear();
			
			if(patternIndex >= patterns.size())
				return false;
						
			tokens = patterns.get(patternIndex++).pattern;
			return true;
		}
		
		
		@Override
		public String toString() {
			
			return token;
		}
	}
}
