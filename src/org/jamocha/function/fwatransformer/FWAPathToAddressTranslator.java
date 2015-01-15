package org.jamocha.function.fwatransformer;

import java.util.Collection;
import java.util.function.IntFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FWAPathToAddressTranslator implements FunctionWithArgumentsVisitor<PathLeaf> {
	private final Collection<SlotInFactAddress> addresses;
	private FunctionWithArguments<ParameterLeaf> functionWithArguments;

	public static FunctionWithArguments<ParameterLeaf> translate(final FunctionWithArguments<PathLeaf> fwa,
			final Collection<SlotInFactAddress> addresses) {
		return fwa.accept(new FWAPathToAddressTranslator(addresses)).functionWithArguments;
	}

	public static PredicateWithArguments<ParameterLeaf> translate(final PredicateWithArguments<PathLeaf> fwa,
			final Collection<SlotInFactAddress> addresses) {
		return fwa.accept(new FWAPathToAddressTranslator.PWAPathToAddressTranslator(addresses)).functionWithArguments;
	}

	public FWAPathToAddressTranslator(final Collection<SlotInFactAddress> addresses) {
		this.addresses = addresses;
	}

	public FunctionWithArguments<ParameterLeaf> getFunctionWithArguments() {
		return this.functionWithArguments;
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite<PathLeaf> functionWithArgumentsComposite) {
		this.functionWithArguments =
				new FunctionWithArgumentsComposite<>(functionWithArgumentsComposite.getFunction(), translateArgs(
						functionWithArgumentsComposite.getArgs(), this.addresses));
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<PathLeaf> predicateWithArgumentsComposite) {
		this.functionWithArguments =
				new PredicateWithArgumentsComposite<>(predicateWithArgumentsComposite.getFunction(), translateArgs(
						predicateWithArgumentsComposite.getArgs(), this.addresses));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final ConstantLeaf<PathLeaf> constantLeaf) {
		this.functionWithArguments = (ConstantLeaf<ParameterLeaf>) (ConstantLeaf<?>) constantLeaf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final GlobalVariableLeaf<PathLeaf> globalVariableLeaf) {
		this.functionWithArguments = (GlobalVariableLeaf<ParameterLeaf>) (GlobalVariableLeaf<?>) globalVariableLeaf;
	}

	@Override
	public void visit(final PathLeaf pathLeaf) {
		final FactAddress factAddressInCurrentlyLowestNode = pathLeaf.getPath().getFactAddressInCurrentlyLowestNode();
		this.addresses.add(new SlotInFactAddress(factAddressInCurrentlyLowestNode, pathLeaf.getSlot()));
		this.functionWithArguments = new ParameterLeaf(pathLeaf.getReturnType(), pathLeaf.hash());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final Assert<PathLeaf> fwa) {
		this.functionWithArguments =
				new Assert<>(fwa.getNetwork(), (Assert.TemplateContainer<ParameterLeaf>[]) translateArgs(fwa.getArgs(),
						this.addresses, Assert.TemplateContainer[]::new));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final Modify<PathLeaf> fwa) {
		this.functionWithArguments =
				new Modify<>(fwa.getNetwork(), translateArg(fwa.getTargetFact(), this.addresses),
						(Modify.SlotAndValue<ParameterLeaf>[]) translateArgs(fwa.getArgs(), this.addresses,
								Modify.SlotAndValue[]::new));
	}

	@Override
	public void visit(final Retract<PathLeaf> fwa) {
		this.functionWithArguments = new Retract<>(fwa.getNetwork(), translateArgs(fwa.getArgs(), this.addresses));
	}

	@Override
	public void visit(final Assert.TemplateContainer<PathLeaf> fwa) {
		this.functionWithArguments =
				new Assert.TemplateContainer<>(fwa.getTemplate(), translateArgs(fwa.getArgs(), this.addresses));
	}

	@Override
	public void visit(final Modify.SlotAndValue<PathLeaf> fwa) {
		this.functionWithArguments =
				new Modify.SlotAndValue<>(fwa.getSlotName(), translateArg(fwa.getValue(), this.addresses));
	}

	@SuppressWarnings("unchecked")
	private static FunctionWithArguments<ParameterLeaf>[] translateArgs(
			final FunctionWithArguments<PathLeaf>[] originalArgs, final Collection<SlotInFactAddress> addresses) {
		return (FunctionWithArguments<ParameterLeaf>[]) translateArgs(originalArgs, addresses,
				FunctionWithArguments[]::new);
	}

	private static FunctionWithArguments<ParameterLeaf> translateArg(final FunctionWithArguments<PathLeaf> originalArg,
			final Collection<SlotInFactAddress> addresses) {
		return originalArg.accept(new FWAPathToAddressTranslator(addresses)).getFunctionWithArguments();
	}

	@SuppressWarnings("unchecked")
	private static <T extends FunctionWithArguments<?>> T[] translateArgs(final T[] originalArgs,
			final Collection<SlotInFactAddress> addresses, final IntFunction<T[]> array) {
		final int numArgs = originalArgs.length;
		final T[] translatedArgs = array.apply(numArgs);
		for (int i = 0; i < numArgs; ++i) {
			final T originalArg = originalArgs[i];
			translatedArgs[i] = (T) translateArg((FunctionWithArguments<PathLeaf>) originalArg, addresses);
		}
		return translatedArgs;
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	public static class PWAPathToAddressTranslator implements DefaultFunctionWithArgumentsVisitor<PathLeaf> {
		private final Collection<SlotInFactAddress> addresses;
		@Getter
		private PredicateWithArguments<ParameterLeaf> functionWithArguments;

		@Override
		public void visit(final PredicateWithArgumentsComposite<PathLeaf> predicateWithArgumentsComposite) {
			this.functionWithArguments =
					new PredicateWithArgumentsComposite<>(predicateWithArgumentsComposite.getFunction(), translateArgs(
							predicateWithArgumentsComposite.getArgs(), this.addresses));
		}

		@Override
		public void defaultAction(final FunctionWithArguments<PathLeaf> function) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}
	}

}