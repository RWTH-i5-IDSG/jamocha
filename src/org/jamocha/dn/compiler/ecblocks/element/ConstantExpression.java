package org.jamocha.dn.compiler.ecblocks.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = { "constant" })
@ToString(of = { "constant" })
public class ConstantExpression implements Element {
	final FunctionWithArguments<ECLeaf> constant;
	final ConstantLeaf<TemplateSlotLeaf> templateSlotLeaf;
	@Getter(onMethod = @__({ @Override }))
	final EquivalenceClass equivalenceClass;

	public ConstantExpression(final FunctionWithArguments<ECLeaf> constant, final EquivalenceClass equivalenceClass) {
		this.constant = constant;
		this.templateSlotLeaf = new ConstantLeaf<>(constant);
		this.equivalenceClass = equivalenceClass;
	}

	@Override
	public SingleFactVariable getFactVariable() {
		return null;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.CONSTANT_EXPRESSION;
	}

	@Override
	public <V extends ElementVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}
}