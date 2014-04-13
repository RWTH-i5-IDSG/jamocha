package org.jamocha.filter.fwa;

import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArgumentsVisitor extends FunctionWithArgumentsCompositeVisitor {
	public void visit(final ConstantLeaf constantLeaf);

	public void visit(final ParameterLeaf parameterLeaf);

	public void visit(final PathLeaf pathLeaf);

}