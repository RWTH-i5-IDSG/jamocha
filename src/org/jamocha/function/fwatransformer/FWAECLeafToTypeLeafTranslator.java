package org.jamocha.function.fwatransformer;

import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

public class FWAECLeafToTypeLeafTranslator extends FWATranslator<ECLeaf, TypeLeaf> {
	public static PredicateWithArguments<TypeLeaf> translate(final PredicateWithArguments<ECLeaf> predicate) {
		final FWAECLeafToTypeLeafTranslator instance = new FWAECLeafToTypeLeafTranslator();
		predicate.accept(instance);
		return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
	}

	@Override
	public FWATranslator<ECLeaf, TypeLeaf> of() {
		return new FWAECLeafToTypeLeafTranslator();
	}

	@Override
	public void visit(final ECLeaf leaf) {
		this.functionWithArguments = new TypeLeaf(leaf.getReturnType());
	}
}