package org.jamocha.function.fwa;

import org.jamocha.visitor.Visitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArgumentsVisitor<L extends ExchangeableLeaf<L>> extends Visitor {
	public void visit(final FunctionWithArgumentsComposite<L> functionWithArgumentsComposite);

	public void visit(final PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite);

	public void visit(final ConstantLeaf<L> constantLeaf);

	public void visit(final GlobalVariableLeaf<L> globalVariableLeaf);

	public void visit(final L leaf);

	public void visit(final Assert<L> fwa);

	public void visit(final Assert.TemplateContainer<L> fwa);

	public void visit(final Retract<L> fwa);

	public void visit(final Modify<L> fwa);

	public void visit(final Modify.SlotAndValue<L> fwa);
}