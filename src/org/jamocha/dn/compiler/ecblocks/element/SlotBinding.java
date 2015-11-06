package org.jamocha.dn.compiler.ecblocks.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class SlotBinding implements Element {
	final SingleSlotVariable slot;
	final TemplateSlotLeaf templateSlotLeaf;

	public SlotBinding(final SingleSlotVariable slot) {
		this.slot = slot;
		this.templateSlotLeaf = new TemplateSlotLeaf(getFactVariable().getTemplate(), slot.getSlot());
	}

	@Override
	public EquivalenceClass getEquivalenceClass() {
		return this.slot.getEqual();
	}

	@Override
	public SingleFactVariable getFactVariable() {
		return this.slot.getFactVariable();
	}

	@Override
	public ElementType getElementType() {
		return ElementType.SLOT_BINDING;
	}

	public TemplateSlotLeaf getTemplateSlotLeaf() {
		return templateSlotLeaf;
	}

	@Override
	public <V extends ElementVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}
}