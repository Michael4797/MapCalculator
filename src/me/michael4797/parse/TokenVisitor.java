package me.michael4797.parse;

public interface TokenVisitor<T> {

	/**
	 * Visits the specified token, Only called if a more specific visit
	 * method cannot be found. More specific visit methods can be declared
	 * as public methods with the same return type as this method and a
	 * single Token parameter. These methods must be named visitToken,
	 * where Token is the name of the Token to be visited.
	 * For example,<br/>
	 * {@code public T visitIdentifier(Token token)}<br/>
	 * would be called upon accepting an Identifier Token.
	 * @param token The token that is being visited.
	 */
	public T visit(Token token);
}
