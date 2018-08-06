package me.michael4797.udmf;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Visits a Value token in the UDMF TEXTMAP lump and returns it as a Value object to be used in a Block property.
 */
public class ValueVisitor implements TokenVisitor<Value>{

	@Override
	public Value visit(Token token) {
		
		throw new RuntimeException("Attempted to visit invalid token type " + token);
	}

	
	public Value visitValue(Token token) {
		
		return token.getChild(0).accept(this); //Visits the value
	}

	
	public Value visitBoolean(Token token) {
		
		return new BooleanValue(token.value.toLowerCase().equals("true")); //Return the boolean as a BooleanValue
	}

	
	public Value visitInteger(Token token) {
		
		return new IntegerValue(Integer.parseInt(token.value)); //Return the integer as an IntegerValue
	}

	
	public Value visitFloat(Token token) {
		
		return new FloatValue(Double.parseDouble(token.value)); //Return the float as a FloatValue
	}

	
	public Value visitQuotedString(Token token) {
		
		return new StringValue(token.value.substring(1, token.value.length()-1)); //Return the String as a StringValue
	}
}
