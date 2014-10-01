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
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FWAPathToAddressTranslator implements FunctionWithArgumentsVisitor {
	private final Collection<SlotInFactAddress> addresses;
	private FunctionWithArguments functionWithArguments;

	public static FunctionWithArguments translate(final FunctionWithArguments fwa,
			final Collection<SlotInFactAddress> addresses) {
		return fwa.accept(new FWAPathToAddressTranslator(addresses)).functionWithArguments;
	}

	public static PredicateWithArguments translate(final PredicateWithArguments fwa,
			final Collection<SlotInFactAddress> addresses) {
		return fwa.accept(new FWAPathToAddressTranslator.PWAPathToAddressTranslator(addresses)).functionWithArguments;
	}

	public FWAPathToAddressTranslator(final Collection<SlotInFactAddress> addresses) {
		this.addresses = addresses;
	}

	public FunctionWithArguments getFunctionWithArguments() {
		return this.functionWithArguments;
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
		this.functionWithArguments =
				new FunctionWithArgumentsComposite(functionWithArgumentsComposite.getFunction(),
						translateArgs(functionWithArgumentsComposite.getArgs(), this.addresses));
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
		this.functionWithArguments =
				new PredicateWithArgumentsComposite(predicateWithArgumentsComposite.getFunction(),
						translateArgs(predicateWithArgumentsComposite.getArgs(), this.addresses));
	}

	@Override
	public void visit(final ConstantLeaf constantLeaf) {
		this.functionWithArguments = constantLeaf;
	}

	@Override
	public void visit(final ParameterLeaf parameterLeaf) {
		this.functionWithArguments = parameterLeaf;
	}

	@Override
	public void visit(final PathLeaf pathLeaf) {
		final FactAddress factAddressInCurrentlyLowestNode =
				pathLeaf.getPath().getFactAddressInCurrentlyLowestNode();
		this.addresses.add(new SlotInFactAddress(factAddressInCurrentlyLowestNode, pathLeaf
				.getSlot()));
		this.functionWithArguments = new ParameterLeaf(pathLeaf.getReturnType(), pathLeaf.hash());
	}

	@Override
	public void visit(final SymbolLeaf fwa) {
		throw new UnsupportedOperationException(
				"At this point, the Filter should already have been translated to a PathFilter!");
	}

	@Override
	public void visit(final Assert fwa) {
		this.functionWithArguments =
				new Assert(fwa.getNetwork(), translateArgs(fwa.getArgs(), this.addresses,
						Assert.TemplateContainer[]::new));
	}

	@Override
	public void visit(final Modify fwa) {
		this.functionWithArguments =
				new Modify(fwa.getNetwork(), translateArg(fwa.getTargetFact(), this.addresses),
						translateArgs(fwa.getArgs(), this.addresses, Modify.SlotAndValue[]::new));
	}

	@Override
	public void visit(final Retract fwa) {
		this.functionWithArguments =
				new Retract(fwa.getNetwork(), translateArgs(fwa.getArgs(), this.addresses));
	}

	@Override
	public void visit(final Assert.TemplateContainer fwa) {
		this.functionWithArguments =
				new Assert.TemplateContainer(fwa.getTemplate(), translateArgs(fwa.getArgs(),
						this.addresses));
	}

	@Override
	public void visit(final Modify.SlotAndValue fwa) {
		this.functionWithArguments =
				new Modify.SlotAndValue(fwa.getSlotName(), translateArg(fwa.getValue(),
						this.addresses));
	}

	private static FunctionWithArguments[] translateArgs(
			final FunctionWithArguments[] originalArgs,
			final Collection<SlotInFactAddress> addresses) {
		return translateArgs(originalArgs, addresses, FunctionWithArguments[]::new);
	}

	@SuppressWarnings("unchecked")
	private static <T extends FunctionWithArguments> T translateArg(final T originalArg,
			final Collection<SlotInFactAddress> addresses) {
		return (T) originalArg.accept(new FWAPathToAddressTranslator(addresses))
				.getFunctionWithArguments();
	}

	private static <T extends FunctionWithArguments> T[] translateArgs(final T[] originalArgs,
			final Collection<SlotInFactAddress> addresses, final IntFunction<T[]> array) {
		final int numArgs = originalArgs.length;
		final T[] translatedArgs = array.apply(numArgs);
		for (int i = 0; i < numArgs; ++i) {
			final T originalArg = originalArgs[i];
			translatedArgs[i] = translateArg(originalArg, addresses);
		}
		return translatedArgs;
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	public static class PWAPathToAddressTranslator implements DefaultFunctionWithArgumentsVisitor {
		private final Collection<SlotInFactAddress> addresses;
		@Getter
		private PredicateWithArguments functionWithArguments;

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(
							predicateWithArgumentsComposite.getFunction(), translateArgs(
									predicateWithArgumentsComposite.getArgs(), this.addresses));
		}

		@Override
		public void defaultAction(final FunctionWithArguments function) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}
	}

}