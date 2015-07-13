/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.function.fwatransformer;

import java.util.function.IntFunction;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class FWASymbolToECTranslator implements FunctionWithArgumentsVisitor<SymbolLeaf> {
	private FunctionWithArguments<ECLeaf> functionWithArguments;

	public static PredicateWithArguments<ECLeaf> translate(final PredicateWithArguments<SymbolLeaf> pwa) {
		return (PredicateWithArguments<ECLeaf>) translate((FunctionWithArguments<SymbolLeaf>) pwa);
	}

	public static FunctionWithArguments<ECLeaf> translate(final FunctionWithArguments<SymbolLeaf> pwa) {
		return pwa.accept(new FWASymbolToECTranslator()).functionWithArguments;
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite<SymbolLeaf> functionWithArgumentsComposite) {
		this.functionWithArguments = new FunctionWithArgumentsComposite<>(functionWithArgumentsComposite.getFunction(),
				translateArgs(functionWithArgumentsComposite.getArgs()));
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> predicateWithArgumentsComposite) {
		this.functionWithArguments =
				new PredicateWithArgumentsComposite<>(predicateWithArgumentsComposite.getFunction(),
						translateArgs(predicateWithArgumentsComposite.getArgs()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
		this.functionWithArguments = (ConstantLeaf<ECLeaf>) (ConstantLeaf<?>) constantLeaf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
		this.functionWithArguments = (GlobalVariableLeaf<ECLeaf>) (GlobalVariableLeaf<?>) globalVariableLeaf;
	}

	@Override
	public void visit(final SymbolLeaf symbolLeaf) {
		this.functionWithArguments = new ECLeaf(symbolLeaf.getSymbol().getEqual());
	}

	@Override
	public void visit(final Bind<SymbolLeaf> fwa) {
		this.functionWithArguments = new Bind<>(translateArgs(fwa.getArgs()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final Assert<SymbolLeaf> fwa) {
		this.functionWithArguments = new Assert<>(fwa.getNetwork(),
				(Assert.TemplateContainer<ECLeaf>[]) translateArgs(fwa.getArgs(), Assert.TemplateContainer[]::new));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final Modify<SymbolLeaf> fwa) {
		this.functionWithArguments = new Modify<>(fwa.getNetwork(), translateArg(fwa.getTargetFact()),
				(Modify.SlotAndValue<ECLeaf>[]) translateArgs(fwa.getArgs(), Modify.SlotAndValue[]::new));
	}

	@Override
	public void visit(final Retract<SymbolLeaf> fwa) {
		this.functionWithArguments = new Retract<>(fwa.getNetwork(), translateArgs(fwa.getArgs()));
	}

	@Override
	public void visit(final Assert.TemplateContainer<SymbolLeaf> fwa) {
		this.functionWithArguments = new Assert.TemplateContainer<>(fwa.getTemplate(), translateArgs(fwa.getArgs()));
	}

	@Override
	public void visit(final Modify.SlotAndValue<SymbolLeaf> fwa) {
		this.functionWithArguments = new Modify.SlotAndValue<>(fwa.getSlotName(), translateArg(fwa.getValue()));
	}

	@SuppressWarnings("unchecked")
	private static FunctionWithArguments<ECLeaf>[] translateArgs(
			final FunctionWithArguments<SymbolLeaf>[] originalArgs) {
		return (FunctionWithArguments<ECLeaf>[]) translateArgs(originalArgs, FunctionWithArguments[]::new);
	}

	private static FunctionWithArguments<ECLeaf> translateArg(final FunctionWithArguments<SymbolLeaf> originalArg) {
		return originalArg.accept(new FWASymbolToECTranslator()).getFunctionWithArguments();
	}

	@SuppressWarnings("unchecked")
	private static <T extends FunctionWithArguments<?>> T[] translateArgs(final T[] originalArgs,
			final IntFunction<T[]> array) {
		final int numArgs = originalArgs.length;
		final T[] translatedArgs = array.apply(numArgs);
		for (int i = 0; i < numArgs; ++i) {
			final T originalArg = originalArgs[i];
			translatedArgs[i] = (T) translateArg((FunctionWithArguments<SymbolLeaf>) originalArg);
		}
		return translatedArgs;
	}
}