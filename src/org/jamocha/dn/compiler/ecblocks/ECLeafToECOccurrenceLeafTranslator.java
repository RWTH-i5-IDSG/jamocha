package org.jamocha.dn.compiler.ecblocks;

import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwatransformer.FWATranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

public class ECLeafToECOccurrenceLeafTranslator extends FWATranslator<ECLeaf, ECOccurrenceLeaf> {
	public static FunctionWithArguments<ECOccurrenceLeaf> translateUsingNewOccurrences(
			final FunctionWithArguments<ECLeaf> toTranslate) {
		return toTranslate.accept(new ECLeafToECOccurrenceLeafTranslator()).functionWithArguments;
	}

	@Override
	public void visit(final ECLeaf leaf) {
		final EquivalenceClass ec = leaf.getEc();
		final ECOccurrence occurrence = new ECOccurrence(ec);
		this.functionWithArguments = new ECOccurrenceLeaf(occurrence);
	}

	@Override
	public FWATranslator<ECLeaf, ECOccurrenceLeaf> of() {
		return new ECLeafToECOccurrenceLeafTranslator();
	}
}