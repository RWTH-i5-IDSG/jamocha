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
@EqualsAndHashCode(of = { "functionalExpression" })
@ToString(of = "functionalExpression")
public class FunctionalExpression {
	final FunctionWithArguments<ECOccurrenceLeaf> functionalExpression;
	final FunctionWithArguments<TypeLeaf> lookupKey;
	final EquivalenceClass originEquivalenceClass;

	public FunctionalExpression(final FunctionWithArguments<ECOccurrenceLeaf> functionalExpression,
			final EquivalenceClass originEquivalenceClass) {
		this.functionalExpression = functionalExpression;
		this.lookupKey = FWAECOccurrenceLeafToTypeLeafTranslator.translate(functionalExpression);
		this.originEquivalenceClass = originEquivalenceClass;
	}

	public EquivalenceClass getEquivalenceClass() {
		return this.originEquivalenceClass;
	}
}