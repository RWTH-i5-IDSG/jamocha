/**
 * 
 */
package org.jamocha.rules;

/**
*
* @author Josef Hahn
* @author Karl-Heinz Krempels
* @author Janno von Stülpnagel
* @author Christoph Terwelp
*/
public interface LHSVisitor <T extends Object> {
	public T visit(AndCondition c, T data);

	public T visit(AndConnectedConstraint c, T data);

	public T visit(BoundConstraint c, T data);

	public T visit(ExistsCondition c, T data);

	public T visit(LiteralConstraint c, T data);

	public T visit(NotExistsCondition c, T data);

	public T visit(ObjectCondition c, T data);

	public T visit(OrCondition c, T data);
	
	public T visit(OrConnectedConstraint c, T data);
	
	public T visit(OrderedFactConstraint c, T data);
	
	public T visit(PredicateConstraint c, T data);
	
	public T visit(ReturnValueConstraint c, T data);

	public T visit(TestCondition c, T data);

}
