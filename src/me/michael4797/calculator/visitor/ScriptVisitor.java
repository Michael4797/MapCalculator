package me.michael4797.calculator.visitor;

import java.util.HashSet;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Finds all the tags of sectors used as death pits in a single script.
 */
public class ScriptVisitor implements TokenVisitor<HashSet<Integer>>{

	private static final SectorDamageVisitor sectorDamageVisitor = new SectorDamageVisitor();
	private static final SectorSetDamageVisitor sectorSetDamageVisitor = new SectorSetDamageVisitor();
	
	private final HashSet<Integer> deathPits = new HashSet<>();
	private final HashSet<Integer> potentialDeathPits = new HashSet<>();
	private boolean restarts = false;
	
	
	@Override	
	public HashSet<Integer> visit(Token token) {
		
		return null;
	}
	
	public HashSet<Integer> visitBlock(Token token) {
		
		token.getChild(1).accept(this); //Visits the statement list
		return deathPits;
	}

	
	public HashSet<Integer> visitStatementList(Token token) {
		
		for(int i = 0; i < token.getChildCount(); ++i)
			token.getChild(i).accept(this); //Visits each statement in the list
		
		return null;
	}

	
	public HashSet<Integer> visitBody(Token token) {
		
		token.getChild(0).accept(this);	//Visits a block or a statement	
		return null;
	}

	
	public HashSet<Integer> visitStatement(Token token) {
		
		token.getChild(0).accept(this); //Visits the type of statement
		return null;
	}

	
	public HashSet<Integer> visitControlStatement(Token token) {
		
		token.getChild(0).accept(this);	//Visits the type of control statement
		return null;
	}

	
	public HashSet<Integer> visitKeyword(Token token) {
		
		token.getChild(0).accept(this);	//Visits the type of keyword
		return null;
	}

	
	public HashSet<Integer> visitRestart(Token token) {
		
		//If restart is never called, then sectors used in a SectorDamage call won't be treated as death pits
		if(!restarts) {
			
			deathPits.addAll(potentialDeathPits);
			potentialDeathPits.clear();
		}
		
		restarts = true;
		return null;
	}

	
	public HashSet<Integer> visitExpression(Token token) {
		
		token.getChild(0).accept(this);		
		return null;
	}

	
	public HashSet<Integer> visitFunctionCall(Token token) {
		
		String functionName = token.getChild(0).value;

		if(functionName.equalsIgnoreCase("SectorDamage")) {
		
			Integer tag = token.getChild(2).accept(sectorDamageVisitor);
			
			if(tag == null)
				return null;

			//If restart is never called, then sectors used in a SectorDamage call won't be treated as death pits
			if(restarts)
				deathPits.add(tag);
			else
				potentialDeathPits.add(tag);
		}
		else if(functionName.equalsIgnoreCase("Sector_SetDamage")) {

			//Sectors used in a Sector_SetDamage call are always treated as death pits
			Integer tag = token.getChild(2).accept(sectorSetDamageVisitor);
			if(tag != null)
				deathPits.add(tag);
		}
				
		return null;
	}
}
