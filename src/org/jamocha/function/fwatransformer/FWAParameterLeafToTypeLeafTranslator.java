package org.jamocha.function.fwatransformer;

import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

@RequiredArgsConstructor
public class FWAParameterLeafToTypeLeafTranslator extends FWATranslator<ParameterLeaf, TypeLeaf> {
	@Override
	public FWAParameterLeafToTypeLeafTranslator of() {
		return new FWAParameterLeafToTypeLeafTranslator();
	}

	public static PredicateWithArguments<TypeLeaf> getArguments(final PredicateWithArguments<ParameterLeaf> predicate) {
		final FWAParameterLeafToTypeLeafTranslator instance = new FWAParameterLeafToTypeLeafTranslator();
		predicate.accept(instance);
		return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
	}

	@Override
	public void visit(final ParameterLeaf leaf) {
		this.functionWithArguments = new TypeLeaf(leaf.getType());
	}
}