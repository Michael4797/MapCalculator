package me.michael4797.calculator.visitor;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Finds the sector tag used in a call to Sector_SetDamage.
 */
public class SectorSetDamageVisitor implements TokenVisitor<Integer>{

	
	@Override
	public Integer visit(Token token) {

		System.out.println("Warning: unexpected argument type when parsing Sector_SetDamage " + token.name);
		return null;
	}

	
	public Integer visitEmptyArgumentList(Token token) {

		if(token.getChildCount() < 1)
			return null;
		
		return token.getChild(0).accept(this);
	}

	
	public Integer visitPrintArgumentList(Token token) {

		if(token.getChildCount() < 1)
			return null;
		
		return token.getChild(0).accept(this);
	}

	
	public Integer visitArgumentList(Token token) {

		if(token.getChildCount() < 1)
			return null;

		//We only care about the first argument in the argument list for the function call.
		return token.getChild(0).accept(this);
	}

	
	public Integer visitArgument(Token token) {

		if(token.getChildCount() < 1)
			return null;
		
		return token.getChild(0).accept(this);
	}

	
	public Integer visitTranslation(Token token) {

		return null;
	}

	
	public Integer visitRightHandExpression(Token token) {

		if(token.getChildCount() < 1)
			return null;
		
		return token.getChild(0).accept(this);
	}

	
	public Integer visitSingleValue(Token token) {

		if(token.getChildCount() < 1)
			return null;
		
		return token.getChild(0).accept(this);
	}

	
	public Integer visitInteger(Token token) {

		//We made it to the tag, parse it and return it back.
		return Integer.parseInt(token.value);
	}
}
