package org.jamocha.dn.compiler.ecblocks.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.dn.compiler.ecblocks.ECOccurrenceLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TypeLeaf;
import org.jamocha.function.fwatransformer.FWAECOccurrenceLeafToTypeLeafTranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = { "variableExpression" })
@ToString(of = "variableExpression")
public class VariableExpression {
	final FunctionWithArguments<ECOccurrenceLeaf> variableExpression;
	final FunctionWithArguments<TypeLeaf> lookupKey;
	final EquivalenceClass originEquivalenceClass;

	public VariableExpression(final FunctionWithArguments<ECOccurrenceLeaf> variableExpression,
			final EquivalenceClass originEquivalenceClass) {
		this.variableExpression = variableExpression;
		this.lookupKey = FWAECOccurrenceLeafToTypeLeafTranslator.translate(variableExpression);
		this.originEquivalenceClass = originEquivalenceClass;
	}

	public EquivalenceClass getEquivalenceClass() {
		return this.originEquivalenceClass;
	}
}