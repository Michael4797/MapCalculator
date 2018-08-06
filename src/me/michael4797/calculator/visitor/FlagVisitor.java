package me.michael4797.calculator.visitor;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Parses an OR of flags in a call to SectorDamage to ensure the DAMAGE_PLAYERS flag is set.
 * If this flag is not set, then we don't care about this call since it doesn't affect our
 * playable area.
 */
public class FlagVisitor implements TokenVisitor<Boolean>{

	
	@Override
	public Boolean visit(Token token) {

		System.out.println("Warning: Unexpected token when parsing flags " + token.name);
		return false;
	}
	
	
	public Boolean visitBinaryOrExpression(Token token) {

		//We found an OR, so check each expression in the OR and look for the flag we care about
		for(int i = 0; i < token.getChildCount(); i += 2)
			if(token.getChild(i).accept(this)) 
				return true;
		
		return false;
	}
	
	
	public Boolean visitUnaryExpression(Token token) {

		if(token.getChildCount() > 0)
			return token.getChild(0).accept(this);
		
		return false;
	}
	
	
	public Boolean visitLeftHandOperand(Token token) {

		if(token.getChildCount() > 0)
			return token.getChild(0).accept(this);
		
		return false;
	}
	
	
	public Boolean visitIdentifier(Token token) {

		//We made it to an individual flag, check if we found the one we care about.
		return token.value.equalsIgnoreCase("DAMAGE_PLAYERS");
	}
}
