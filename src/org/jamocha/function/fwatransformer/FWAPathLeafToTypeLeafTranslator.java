package org.jamocha.function.fwatransformer;

import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

public class FWAPathLeafToTypeLeafTranslator extends FWATranslator<ECLeaf, TypeLeaf> {
	public static PredicateWithArguments<TypeLeaf> getArguments(final PredicateWithArguments<ECLeaf> predicate) {
		final FWAPathLeafToTypeLeafTranslator instance = new FWAPathLeafToTypeLeafTranslator();
		predicate.accept(instance);
		return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
	}

	@Override
	public FWATranslator<ECLeaf, TypeLeaf> of() {
		return new FWAPathLeafToTypeLeafTranslator();
	}

	@Override
	public void visit(final ECLeaf leaf) {
		this.functionWithArguments = new TypeLeaf(leaf.getReturnType());
	}
}