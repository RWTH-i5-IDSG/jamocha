package org.jamocha.function.fwatransformer;

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Map;
import java.util.function.IntFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.RHSVariableLeaf;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwa.VariableValueContext;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class FWASymbolToRHSVariableLeafTranslator implements FunctionWithArgumentsVisitor<SymbolLeaf> {
	final Map<EquivalenceClass, PathLeaf> ec2PathLeaf;
	final VariableValueContext context;
	private FunctionWithArguments<RHSVariableLeaf> functionWithArguments;

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static FunctionWithArguments<RHSVariableLeaf>[] translate(final Map<EquivalenceClass, PathLeaf> ec2PathLeaf,
			final VariableValueContext context, final FunctionWithArguments<SymbolLeaf>... actions) {
		final FWASymbolToRHSVariableLeafTranslator instance =
				new FWASymbolToRHSVariableLeafTranslator(ec2PathLeaf, context);
		return toArray(Arrays.stream(actions).map(fwa -> fwa.accept(instance).functionWithArguments),
				FunctionWithArguments[]::new);
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite<SymbolLeaf> functionWithArgumentsComposite) {
		this.functionWithArguments =
				new FunctionWithArgumentsComposite<>(functionWithArgumentsComposite.getFunction(), translateArgs(
						functionWithArgumentsComposite.getArgs(), this.ec2PathLeaf, this.context));
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> predicateWithArgumentsComposite) {
		this.functionWithArguments =
				new PredicateWithArgumentsComposite<>(predicateWithArgumentsComposite.getFunction(), translateArgs(
						predicateWithArgumentsComposite.getArgs(), this.ec2PathLeaf, this.context));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
		this.functionWithArguments = (ConstantLeaf<RHSVariableLeaf>) (ConstantLeaf<?>) constantLeaf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
		this.functionWithArguments = (GlobalVariableLeaf<RHSVariableLeaf>) (GlobalVariableLeaf<?>) globalVariableLeaf;
	}

	@Override
	public void visit(final SymbolLeaf symbolLeaf) {
		final VariableSymbol symbol = symbolLeaf.getSymbol();
		final PathLeaf pathLeaf = ec2PathLeaf.get(symbol.getEqual());
		if (pathLeaf != null) {
			// variable is bound on the LHS, add initializer
			final SlotInFactAddress slotInFactAddress =
					new SlotInFactAddress(pathLeaf.getPath().getFactAddressInCurrentlyLowestNode(), pathLeaf.getSlot());
			context.addInitializer(symbol, slotInFactAddress);
		}
		this.functionWithArguments = new RHSVariableLeaf(context, symbol, symbolLeaf.getReturnType());
	}

	@Override
	public void visit(final Bind<SymbolLeaf> fwa) {
		this.functionWithArguments = new Bind<>(translateArgs(fwa.getArgs(), this.ec2PathLeaf, this.context));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final Assert<SymbolLeaf> fwa) {
		this.functionWithArguments =
				new Assert<>(fwa.getNetwork(), (Assert.TemplateContainer<RHSVariableLeaf>[]) translateArgs(
						fwa.getArgs(), this.ec2PathLeaf, this.context, Assert.TemplateContainer[]::new));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final Modify<SymbolLeaf> fwa) {
		this.functionWithArguments =
				new Modify<>(fwa.getNetwork(), translateArg(fwa.getTargetFact(), this.ec2PathLeaf, this.context),
						(Modify.SlotAndValue<RHSVariableLeaf>[]) translateArgs(fwa.getArgs(), this.ec2PathLeaf,
								this.context, Modify.SlotAndValue[]::new));
	}

	@Override
	public void visit(final Retract<SymbolLeaf> fwa) {
		this.functionWithArguments =
				new Retract<>(fwa.getNetwork(), translateArgs(fwa.getArgs(), this.ec2PathLeaf, this.context));
	}

	@Override
	public void visit(final Assert.TemplateContainer<SymbolLeaf> fwa) {
		this.functionWithArguments =
				new Assert.TemplateContainer<>(fwa.getTemplate(), translateArgs(fwa.getArgs(), this.ec2PathLeaf,
						this.context));
	}

	@Override
	public void visit(final Modify.SlotAndValue<SymbolLeaf> fwa) {
		this.functionWithArguments =
				new Modify.SlotAndValue<>(fwa.getSlotName(), translateArg(fwa.getValue(), this.ec2PathLeaf,
						this.context));
	}

	@SuppressWarnings("unchecked")
	private static FunctionWithArguments<RHSVariableLeaf>[] translateArgs(
			final FunctionWithArguments<SymbolLeaf>[] originalArgs, final Map<EquivalenceClass, PathLeaf> ec2PathLeaf,
			final VariableValueContext context) {
		return (FunctionWithArguments<RHSVariableLeaf>[]) translateArgs(originalArgs, ec2PathLeaf, context,
				FunctionWithArguments[]::new);
	}

	private static FunctionWithArguments<RHSVariableLeaf> translateArg(
			final FunctionWithArguments<SymbolLeaf> originalArg, final Map<EquivalenceClass, PathLeaf> ec2PathLeaf,
			final VariableValueContext context) {
		return originalArg.accept(new FWASymbolToRHSVariableLeafTranslator(ec2PathLeaf, context))
				.getFunctionWithArguments();
	}

	@SuppressWarnings("unchecked")
	private static <T extends FunctionWithArguments<?>> T[] translateArgs(final T[] originalArgs,
			final Map<EquivalenceClass, PathLeaf> ec2PathLeaf, final VariableValueContext context,
			final IntFunction<T[]> array) {
		final int numArgs = originalArgs.length;
		final T[] translatedArgs = array.apply(numArgs);
		for (int i = 0; i < numArgs; ++i) {
			final T originalArg = originalArgs[i];
			translatedArgs[i] = (T) translateArg((FunctionWithArguments<SymbolLeaf>) originalArg, ec2PathLeaf, context);
		}
		return translatedArgs;
	}
}