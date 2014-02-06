package org.jamocha.filter.visitor;

import org.jamocha.filter.fwa.ConstantLeaf;
import org.jamocha.filter.fwa.FunctionWithArgumentsComposite;
import org.jamocha.filter.fwa.PathLeaf;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;

import test.jamocha.filter.PredicateWithArgumentsMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArgumentsVisitor extends Visitor {
	public void visit(final ConstantLeaf constantLeaf);

	public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite);

	public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite);

	public void visit(final ParameterLeaf parameterLeaf);

	public void visit(final PathLeaf pathLeaf);

	public void visit(final PredicateWithArgumentsMockup predicateWithArgumentsMockup);
}