package me.michael4797.calculator.visitor;

import java.util.HashSet;

import me.michael4797.parse.Token;
import me.michael4797.parse.TokenVisitor;

/**
 * Visits a map's scripts lump to find the tags of sectors that are used as death pits.
 */
public class MapScriptsVisitor implements TokenVisitor<HashSet<Integer>>{

	private final HashSet<Integer> deathPits = new HashSet<>();
	
	@Override
	public HashSet<Integer> visit(Token token) {
		
		return deathPits;
	}

	//The root of the ACS Grammar
	public HashSet<Integer> visitGlobalList(Token token) {
		
		deathPits.clear();
		for(int i = 0; i < token.getChildCount(); ++i)
			token.getChild(i).accept(this); //Visits each global in the list
		
		return deathPits;
	}

	
	public HashSet<Integer> visitGlobal(Token token) {
		
		return token.getChild(0).accept(this);
	}

	
	public HashSet<Integer> visitScript(Token token) {
		
		HashSet<Integer> pits = token.getChild(1).accept(new ScriptVisitor()); //Visits the Script's body with the ScriptVisitor
		if(pits != null)
			deathPits.addAll(pits);
		
		return deathPits;
	}
}
