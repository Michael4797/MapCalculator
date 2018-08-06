package me.michael4797.parse;

import java.util.ArrayDeque;
import java.util.Iterator;

public class TokenIterator implements Iterator<LexerToken>{

	private final ArrayDeque<TokenFrame> tokenStack = new ArrayDeque<>();
	private LexerToken next;
	
	
	public TokenIterator(Token token) {
		
		if(token.getChildCount() == -1) {
		
			next = (LexerToken) token;
		}
		else {
			
			tokenStack.push(new TokenFrame(token));
			next();
		}
	}
	
	
	@Override
	public boolean hasNext() {
		
		return next != null;
	}
	
	
	@Override
	public LexerToken next() {
	
		LexerToken toReturn = next;
		Token child;
		
		OuterLoop:
		do{
			
			TokenFrame frame;
			do {
				
				if(tokenStack.isEmpty()) {
					
					child = null;
					break OuterLoop;
				}
				
				frame = tokenStack.pop();
			}while(frame.index >= frame.token.getChildCount());
			
			child = frame.token.getChild(frame.index++);
			tokenStack.push(frame);
			while(child.getChildCount() > 0) {
				
				frame = new TokenFrame(child);
				tokenStack.push(frame);
				child = child.getChild(frame.index++);
			}
		}while(child.getChildCount() != -1);
		
		next = (LexerToken) child;		
		return toReturn;
	}
	
	
	private static class TokenFrame{
		
		private final Token token;
		private int index;
		
		
		private TokenFrame(Token token) {
			
			this.token = token;
		}
	}
}
