package org.jamocha.dn.compiler.ecblocks.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class FactBinding implements Element {
	final SingleFactVariable fact;
	final TemplateSlotLeaf templateSlotLeaf;

	public FactBinding(final SingleFactVariable fact) {
		this.fact = fact;
		this.templateSlotLeaf = new TemplateSlotLeaf(fact.getTemplate(), null);
	}

	@Override
	public EquivalenceClass getEquivalenceClass() {
		return this.fact.getEqual();
	}

	@Override
	public SingleFactVariable getFactVariable() {
		return this.fact;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.FACT_BINDING;
	}

	@Override
	public <V extends ElementVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}
}