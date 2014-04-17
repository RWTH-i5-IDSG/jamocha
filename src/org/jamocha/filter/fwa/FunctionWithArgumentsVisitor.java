package org.jamocha.filter.fwa;

import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArgumentsVisitor extends Visitor {
	public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite);

	public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite);

	public void visit(final ConstantLeaf constantLeaf);

	public void visit(final ParameterLeaf parameterLeaf);

	public void visit(final PathLeaf pathLeaf);

}