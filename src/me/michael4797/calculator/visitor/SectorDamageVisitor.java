package me.michael4797.calculator.visitor;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Finds the tag of a sector used in a SectorDamage function.
 */
public class SectorDamageVisitor implements TokenVisitor<Integer>{

	private static final FlagVisitor flagVisitor = new FlagVisitor();

	
	@Override
	public Integer visit(Token token) {

		System.out.println("Warning: unexpected argument type when parsing Sector_Damage " + token.name);
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

		//The argument list of the function call is all we care about
		if(token.getChildCount() < 9)
			return null;
		
		//Ensure that the flags in this SectorDamage call allow players to be damaged
		if(token.getChild(8).accept(flagVisitor))
			return token.getChild(0).accept(this); //Return the tag of the sector
		
		return null;
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

		//We made it to the end, so parse the tag and return it back up the chain.
		return Integer.parseInt(token.value);
	}
}
