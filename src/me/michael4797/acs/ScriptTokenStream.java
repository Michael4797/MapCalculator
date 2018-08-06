package me.michael4797.acs;

import java.util.ArrayList;
import java.util.HashMap;

import me.michael4797.parse.Lexer;
import me.michael4797.parse.LexerToken;
import me.michael4797.parse.ParserEndOfStreamException;
import me.michael4797.parse.ParserException;
import me.michael4797.parse.StringStream;
import me.michael4797.parse.TokenStream;

/**
 * A custom TokenStream used by the Scripts Parser in order to handle #define macros and other such constructs.
 */
public class ScriptTokenStream implements TokenStream{

	private final HashMap<String, ArrayList<LexerToken>> macros = new HashMap<>();
	private final Lexer in;
	
	private ArrayList<LexerToken> macro;
	private int index = -1;
	
	
	public ScriptTokenStream(Lexer in) {
		
		this.in = in;
	}
	
	
	@Override
	public LexerToken nextToken() throws ParserException {
		
		//If we're currently returning a macro, keep doing what we're doing
		if(macro != null) {
			
			if(index < macro.size()) {
				
				LexerToken token = macro.get(index++);
				return new LexerToken(token.name, token.value);
			}
			
			macro = null;
		}
		
		LexerToken next = in.nextToken();
		while(next.name.equals("Define") || next.name.equals("Include") || next.name.equals("Library")) {
			
			//If we found a #define
			if(next.name.equals("Define")) {
				
				//Split by whitespace
				String[] split = next.value.split("[\t\r\n \\x00]+");
								
				ArrayList<LexerToken> macro = new ArrayList<>();
				//Combine everything after the name of the define
				StringBuffer buffer = new StringBuffer(split[2]);
				for(int i = 3; i < split.length; ++i)
					buffer.append(split[i]);
				
				//Lex the remaining part of the string into tokens
				Lexer lexer = new Lexer(new StringStream(buffer.toString()), in.rules);
				
				try {
					
					//Add the lexed tokens to our macro
					while(true)
						macro.add(lexer.nextToken());
				}catch(ParserEndOfStreamException e) {}
				
				//Put the macro in our map under the specified name
				macros.put(split[1], macro);
				next = in.nextToken();
			}
			//Completely ignore library and include tokens because we don't do anything with them
			else if (next.name.equals("Library")){
				
				in.nextToken();
				next = in.nextToken();
			}else {
				
				in.nextToken();
				next = in.nextToken();
			}
		}
		
		//If we parsed a #define macro, return the macro instead
		macro = macros.get(next.value);
		if(macro != null) {
		
			index = 1;
			LexerToken token = macro.get(0);
			return new LexerToken(token.name, token.value);	
		}

		return next;
	}

}
