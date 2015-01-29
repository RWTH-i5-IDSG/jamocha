/*
 * Copyright 2002-2014 The Jamocha Team
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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.function.IntFunction;

import lombok.Getter;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;

public class FWADeepCopy<L extends ExchangeableLeaf<L>> implements FunctionWithArgumentsVisitor<L> {

	@Getter
	FunctionWithArguments<L> result;

	@SuppressWarnings("unchecked")
	public static <L extends ExchangeableLeaf<L>, T extends FunctionWithArguments<L>> T copy(final T fwa) {
		return (T) fwa.accept(new FWADeepCopy<>()).getResult();
	}

	@SuppressWarnings("unchecked")
	private static <L extends ExchangeableLeaf<L>, T extends FunctionWithArguments<L>> T[] copyArgs(final T[] args,
			final IntFunction<T[]> gen) {
		return toArray(Arrays.stream(args).map(fwa -> (T) fwa.accept(new FWADeepCopy<>()).result), gen);
	}

	@SuppressWarnings("unchecked")
	private static <L extends ExchangeableLeaf<L>> FunctionWithArguments<L>[] copyArgs(
			final FunctionWithArguments<L>[] args) {
		return copyArgs(args, FunctionWithArguments[]::new);
	}

	@Override
	public void visit(final org.jamocha.function.fwa.PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite) {
		this.result =
				new org.jamocha.function.fwa.PredicateWithArgumentsComposite<>(
						predicateWithArgumentsComposite.getFunction(),
						copyArgs(predicateWithArgumentsComposite.getArgs()));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.FunctionWithArgumentsComposite<L> functionWithArgumentsComposite) {
		this.result =
				new org.jamocha.function.fwa.FunctionWithArgumentsComposite<>(
						functionWithArgumentsComposite.getFunction(),
						copyArgs(functionWithArgumentsComposite.getArgs()));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.ConstantLeaf<L> constantLeaf) {
		this.result =
				new org.jamocha.function.fwa.ConstantLeaf<>(constantLeaf.getValue(), constantLeaf.getReturnType());
	}

	@Override
	public void visit(final GlobalVariableLeaf<L> globalVariableLeaf) {
		this.result = new org.jamocha.function.fwa.GlobalVariableLeaf<>(globalVariableLeaf.getVariable());
	}

	// @Override
	// public void visit(final org.jamocha.function.fwa.PathLeaf.ParameterLeaf<ParameterLeaf>
	// parameterLeaf) {
	// this.result =
	// new org.jamocha.function.fwa.PathLeaf.ParameterLeaf(parameterLeaf.getType(),
	// parameterLeaf.hash());
	// }
	//
	// @Override
	// public void visit(final org.jamocha.function.fwa.PathLeaf<PathLeaf> pathLeaf) {
	// this.result = new org.jamocha.function.fwa.PathLeaf(pathLeaf.getPath(), pathLeaf.getSlot());
	// }

	@Override
	public void visit(final org.jamocha.function.fwa.Bind<L> fwa) {
		this.result = new org.jamocha.function.fwa.Bind<L>(copyArgs(fwa.getArgs()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final org.jamocha.function.fwa.Assert<L> fwa) {
		this.result =
				new org.jamocha.function.fwa.Assert<L>(fwa.getNetwork(), copyArgs(fwa.getArgs(),
						Assert.TemplateContainer[]::new));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Assert.TemplateContainer<L> fwa) {
		this.result =
				new org.jamocha.function.fwa.Assert.TemplateContainer<>(fwa.getTemplate(), copyArgs(fwa.getArgs()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final org.jamocha.function.fwa.Modify<L> fwa) {
		this.result =
				new org.jamocha.function.fwa.Modify<L>(fwa.getNetwork(), fwa.getTargetFact()
						.accept(new FWADeepCopy<>()).result, copyArgs(fwa.getArgs(), Modify.SlotAndValue[]::new));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Retract<L> fwa) {
		this.result = new org.jamocha.function.fwa.Retract<>(fwa.getNetwork(), copyArgs(fwa.getArgs()));
	}

	@Override
	public void visit(final L fwa) {
		this.result = fwa.copy();
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Modify.SlotAndValue<L> fwa) {
		this.result =
				new org.jamocha.function.fwa.Modify.SlotAndValue<>(fwa.getSlotName(), fwa.getValue().accept(
						new FWADeepCopy<>()).result);
	}
}