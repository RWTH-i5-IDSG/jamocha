package org.jamocha.dn.compiler.ecblocks.element;

import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.visitor.Visitable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface Element extends Visitable<ElementVisitor> {
	public EquivalenceClass getEquivalenceClass();

	public SingleFactVariable getFactVariable();

	public ElementType getElementType();

	public FunctionWithArguments<TemplateSlotLeaf> getTemplateSlotLeaf();
}