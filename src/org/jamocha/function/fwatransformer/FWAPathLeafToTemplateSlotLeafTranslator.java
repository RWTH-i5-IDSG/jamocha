package org.jamocha.function.fwatransformer;

import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;

public class FWAPathLeafToTemplateSlotLeafTranslator extends FWATranslator<PathLeaf, TemplateSlotLeaf> {
	public static PredicateWithArguments<TemplateSlotLeaf> getArguments(final PredicateWithArguments<PathLeaf> predicate) {
		final FWAPathLeafToTemplateSlotLeafTranslator instance = new FWAPathLeafToTemplateSlotLeafTranslator();
		predicate.accept(instance);
		return (PredicateWithArguments<TemplateSlotLeaf>) instance.functionWithArguments;
	}

	@Override
	public FWAPathLeafToTemplateSlotLeafTranslator of() {
		return new FWAPathLeafToTemplateSlotLeafTranslator();
	}

	@Override
	public void visit(final PathLeaf leaf) {
		this.functionWithArguments = new TemplateSlotLeaf(leaf.getPath().getTemplate(), leaf.getSlot());
	}
}