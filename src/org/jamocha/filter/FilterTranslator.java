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
package org.jamocha.filter;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.jamocha.dn.memory.CounterColumn;
import org.jamocha.dn.memory.CounterColumnMatcher;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.AddressFilter.ExistentialAddressFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.fwa.Assert;
import org.jamocha.filter.fwa.ConstantLeaf;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.FunctionWithArgumentsComposite;
import org.jamocha.filter.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.filter.fwa.Modify;
import org.jamocha.filter.fwa.Modify.SlotAndValue;
import org.jamocha.filter.fwa.PathLeaf;
import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.filter.fwa.Retract;
import org.jamocha.filter.fwa.SymbolLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FilterTranslator {
	public static AddressFilter translate(final PathFilter pathFilter,
			final CounterColumnMatcher filterElementToCounterColumn) {
		return translate(pathFilter, pathFilter.normalise(), filterElementToCounterColumn);
	}

	public static AddressFilter translate(final PathFilter pathFilter,
			final PathFilter normalisedVersion,
			final CounterColumnMatcher filterElementToCounterColumn) {
		return new AddressFilter(toFactAddressSet(pathFilter.getPositiveExistentialPaths()),
				toFactAddressSet(pathFilter.getNegativeExistentialPaths()), translateFEs(
						pathFilter, filterElementToCounterColumn), translateFEs(normalisedVersion,
						filterElementToCounterColumn));
	}

	private static AddressFilterElement[] translateFEs(final PathFilter pathFilter,
			final CounterColumnMatcher filterElementToCounterColumn) {
		final PathFilterElement[] pathFEs = pathFilter.getFilterElements();
		final AddressFilterElement[] addrFEs = new AddressFilterElement[pathFEs.length];
		for (int i = 0; i < pathFEs.length; i++) {
			final PathFilterElement pathFE = pathFEs[i];
			addrFEs[i] = translate(pathFE, filterElementToCounterColumn.getCounterColumn(pathFE));
		}
		return addrFEs;
	}

	static Set<FactAddress> toFactAddressSet(final Set<Path> existentialPaths) {
		return existentialPaths.stream().map(Path::getFactAddressInCurrentlyLowestNode)
				.collect(Collectors.toSet());
	}

	private static AddressFilterElement translate(final PathFilterElement pathFilterElement,
			final CounterColumn counterColumn) {
		final ArrayList<SlotInFactAddress> addresses = new ArrayList<>();
		final PredicateWithArguments predicateWithArguments =
				pathFilterElement.getFunction()
						.accept(new PredicateWithArgumentsTranslator(addresses))
						.getFunctionWithArguments();
		final SlotInFactAddress[] addressArray =
				addresses.toArray(new SlotInFactAddress[addresses.size()]);
		if (null == counterColumn)
			return new AddressFilterElement(predicateWithArguments, addressArray);
		return new ExistentialAddressFilterElement(predicateWithArguments, addressArray,
				counterColumn);
	}

	@SuppressWarnings("unchecked")
	private static <T extends FunctionWithArguments> T translateArg(final T originalArg,
			final Collection<SlotInFactAddress> addresses) {
		return (T) originalArg.accept(new FunctionWithArgumentsTranslator(addresses))
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

	private static FunctionWithArguments[] translateArgs(
			final FunctionWithArguments[] originalArgs,
			final Collection<SlotInFactAddress> addresses) {
		return translateArgs(originalArgs, addresses, FunctionWithArguments[]::new);
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	private static class PredicateWithArgumentsTranslator implements FunctionWithArgumentsVisitor {
		private final Collection<SlotInFactAddress> addresses;
		private PredicateWithArguments functionWithArguments;

		private PredicateWithArgumentsTranslator(final Collection<SlotInFactAddress> addresses) {
			this.addresses = addresses;
		}

		public PredicateWithArguments getFunctionWithArguments() {
			return this.functionWithArguments;
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(
							predicateWithArgumentsComposite.getFunction(), translateArgs(
									predicateWithArgumentsComposite.getArgs(), this.addresses));
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final Assert fwa) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final Assert.TemplateContainer fwa) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final Retract fwa) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final Modify fwa) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}

		@Override
		public void visit(final SymbolLeaf fwa) {
			throw new UnsupportedOperationException(
					"PredicateWithArgumentsTranslator is only to be used with PredicateWithArguments!");
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	private static class FunctionWithArgumentsTranslator implements FunctionWithArgumentsVisitor {
		private final Collection<SlotInFactAddress> addresses;
		private FunctionWithArguments functionWithArguments;

		private FunctionWithArgumentsTranslator(final Collection<SlotInFactAddress> addresses) {
			this.addresses = addresses;
		}

		public FunctionWithArguments getFunctionWithArguments() {
			return this.functionWithArguments;
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			this.functionWithArguments =
					new FunctionWithArgumentsComposite(
							functionWithArgumentsComposite.getFunction(), translateArgs(
									functionWithArgumentsComposite.getArgs(), this.addresses));
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(
							predicateWithArgumentsComposite.getFunction(), translateArgs(
									predicateWithArgumentsComposite.getArgs(), this.addresses));

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
			this.functionWithArguments =
					new ParameterLeaf(pathLeaf.getReturnType(), pathLeaf.hash());
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
							toArray(Arrays.stream(fwa.getArgs()).map(
									sav -> new SlotAndValue(sav.getSlotName(), translateArg(
											sav.getValue(), this.addresses))), SlotAndValue[]::new));
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
	}
}
