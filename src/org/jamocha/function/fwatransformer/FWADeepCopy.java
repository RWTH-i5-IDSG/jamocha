package org.jamocha.function.fwatransformer;

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.function.IntFunction;

import lombok.Getter;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.Modify;

public class FWADeepCopy implements FunctionWithArgumentsVisitor {
	@Getter
	FunctionWithArguments result;

	public static FunctionWithArguments copy(final FunctionWithArguments fwa) {
		return fwa.accept(new FWADeepCopy()).getResult();
	}

	@SuppressWarnings("unchecked")
	private static <T extends FunctionWithArguments> T[] copyArgs(final T[] args,
			final IntFunction<T[]> gen) {
		return toArray(Arrays.stream(args).map(fwa -> (T) fwa.accept(new FWADeepCopy()).result),
				gen);
	}

	private static FunctionWithArguments[] copyArgs(final FunctionWithArguments[] args) {
		return copyArgs(args, FunctionWithArguments[]::new);
	}

	@Override
	public void visit(
			final org.jamocha.function.fwa.PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
		this.result =
				new org.jamocha.function.fwa.PredicateWithArgumentsComposite(
						predicateWithArgumentsComposite.getFunction(),
						copyArgs(predicateWithArgumentsComposite.getArgs()));
	}

	@Override
	public void visit(
			final org.jamocha.function.fwa.FunctionWithArgumentsComposite functionWithArgumentsComposite) {
		this.result =
				new org.jamocha.function.fwa.FunctionWithArgumentsComposite(
						functionWithArgumentsComposite.getFunction(),
						copyArgs(functionWithArgumentsComposite.getArgs()));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.ConstantLeaf constantLeaf) {
		this.result =
				new org.jamocha.function.fwa.ConstantLeaf(constantLeaf.getValue(),
						constantLeaf.getReturnType());
	}

	@Override
	public void visit(final org.jamocha.function.fwa.PathLeaf.ParameterLeaf parameterLeaf) {
		this.result =
				new org.jamocha.function.fwa.PathLeaf.ParameterLeaf(parameterLeaf.getType(),
						parameterLeaf.hash());
	}

	@Override
	public void visit(final org.jamocha.function.fwa.PathLeaf pathLeaf) {
		this.result = new org.jamocha.function.fwa.PathLeaf(pathLeaf.getPath(), pathLeaf.getSlot());
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Assert fwa) {
		this.result =
				new org.jamocha.function.fwa.Assert(fwa.getNetwork(), copyArgs(fwa.getArgs(),
						Assert.TemplateContainer[]::new));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Assert.TemplateContainer fwa) {
		this.result =
				new org.jamocha.function.fwa.Assert.TemplateContainer(fwa.getTemplate(),
						copyArgs(fwa.getArgs()));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Modify fwa) {
		this.result =
				new org.jamocha.function.fwa.Modify(fwa.getNetwork(), fwa.getTargetFact().accept(
						new FWADeepCopy()).result, copyArgs(fwa.getArgs(),
						Modify.SlotAndValue[]::new));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Retract fwa) {
		this.result =
				new org.jamocha.function.fwa.Retract(fwa.getNetwork(), copyArgs(fwa.getArgs()));
	}

	@Override
	public void visit(final org.jamocha.function.fwa.SymbolLeaf fwa) {
		this.result = new org.jamocha.function.fwa.SymbolLeaf(fwa.getSymbol());
	}

	@Override
	public void visit(final org.jamocha.function.fwa.Modify.SlotAndValue fwa) {
		this.result =
				new org.jamocha.function.fwa.Modify.SlotAndValue(fwa.getSlotName(), fwa.getValue()
						.accept(new FWADeepCopy()).result);
	}
}