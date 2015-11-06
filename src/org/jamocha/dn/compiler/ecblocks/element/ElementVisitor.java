package org.jamocha.dn.compiler.ecblocks.element;

import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ElementVisitor extends Visitor {
	public void visit(final FactBinding element);

	public void visit(final SlotBinding element);

	public void visit(final ConstantExpression element);
}