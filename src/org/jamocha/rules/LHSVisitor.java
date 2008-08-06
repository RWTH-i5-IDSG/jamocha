/**
 * 
 */
package org.jamocha.rules;

/**
*
* @author Josef Hahn
* @author Karl-Heinz Krempels
* @author Janno von St�lpnagel
* @author Christoph Terwelp
*/
public interface LHSVisitor <T extends Object, S extends Object> {
	public S visit(AndCondition c, T data);

	//public S visit(AndConnectedConstraint c, T data);

	//public S visit(BoundConstraint c, T data);

	public S visit(ExistsCondition c, T data);

	//public S visit(LiteralConstraint c, T data);

	public S visit(NotExistsCondition c, T data);

	public S visit(ObjectCondition c, T data);

	public S visit(OrCondition c, T data);
	
	//public S visit(OrConnectedConstraint c, T data);
	
	//public S visit(OrderedFactConstraint c, T data);
	
	//public S visit(PredicateConstraint c, T data);
	
	//public S visit(ReturnValueConstraint c, T data);

	public S visit(TestCondition c, T data);

}
