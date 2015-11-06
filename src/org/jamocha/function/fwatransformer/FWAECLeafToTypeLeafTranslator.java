package org.jamocha.function.fwatransformer;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

public class FWAECLeafToTypeLeafTranslator extends FWATranslator<ECLeaf, TypeLeaf> {
	public static PredicateWithArguments<TypeLeaf> translate(final PredicateWithArguments<ECLeaf> predicate) {
		final FWAECLeafToTypeLeafTranslator instance = new FWAECLeafToTypeLeafTranslator();
		predicate.accept(instance);
		return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
	}

	public static FunctionWithArguments<TypeLeaf> translate(final FunctionWithArguments<ECLeaf> function) {
		final FWAECLeafToTypeLeafTranslator instance = new FWAECLeafToTypeLeafTranslator();
		function.accept(instance);
		return instance.functionWithArguments;
	}

	@Override
	public FWATranslator<ECLeaf, TypeLeaf> of() {
		return new FWAECLeafToTypeLeafTranslator();
	}

	@Override
	public void visit(final ECLeaf leaf) {
		final FunctionWithArguments<ECLeaf> peek = leaf.getEc().getConstantExpressions().peek();
		if (null != peek) {
			this.functionWithArguments = new ConstantLeaf<TypeLeaf>(peek.evaluate(), peek.getReturnType());
		} else {
			this.functionWithArguments = new TypeLeaf(leaf.getReturnType());
		}
	}
}