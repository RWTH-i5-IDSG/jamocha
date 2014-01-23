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

import java.util.ArrayList;
import java.util.Collection;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

import test.jamocha.filter.PredicateWithArgumentsMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FilterTranslator {
	public static AddressFilter translate(final PathFilter pathFilter) {
		final PathFilterElement[] pathFilterElements = pathFilter.getFilterElements();
		final AddressFilterElement[] addressFilterElements =
				new AddressFilterElement[pathFilterElements.length];
		for (int i = 0; i < pathFilterElements.length; i++) {
			addressFilterElements[i] = translate(pathFilterElements[i]);
		}
		return new AddressFilter(addressFilterElements);
	}

	private static AddressFilterElement translate(final PathFilterElement pathFilterElement) {
		final ArrayList<SlotInFactAddress> addresses = new ArrayList<>();
		final PredicateWithArguments predicateWithArguments =
				pathFilterElement.getFunction()
						.accept(new PredicateWithArgumentsTranslator(addresses))
						.getFunctionWithArguments();
		return new AddressFilterElement(predicateWithArguments,
				addresses.toArray(new SlotInFactAddress[addresses.size()]));
	}

	private static class PredicateWithArgumentsTranslator implements Visitor {
		private final Collection<SlotInFactAddress> addresses;
		private PredicateWithArguments functionWithArguments;

		private PredicateWithArgumentsTranslator(final Collection<SlotInFactAddress> addresses) {
			this.addresses = addresses;
		}

		/**
		 * @return the functionWithArguments
		 */
		public PredicateWithArguments getFunctionWithArguments() {
			return this.functionWithArguments;
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			final FunctionWithArguments[] originalArgs = predicateWithArgumentsComposite.args;
			final int numArgs = originalArgs.length;
			final FunctionWithArguments[] translatedArgs = new FunctionWithArguments[numArgs];
			for (int i = 0; i < numArgs; ++i) {
				final FunctionWithArguments originalArg = originalArgs[i];
				translatedArgs[i] =
						originalArg.accept(new FunctionWithArgumentsTranslator(this.addresses))
								.getFunctionWithArguments();
			}
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(predicateWithArgumentsComposite.function,
							translatedArgs);
		}

		@Override
		public void visit(final PredicateWithArgumentsMockup predicateWithArgumentsMockup) {
			this.functionWithArguments =
					new PredicateWithArgumentsMockup(predicateWithArgumentsMockup.isReturnValue(),
							predicateWithArgumentsMockup.getPaths());
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

	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	private static class FunctionWithArgumentsTranslator implements Visitor {
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
					new FunctionWithArgumentsComposite(functionWithArgumentsComposite.function,
							translateArgs(functionWithArgumentsComposite.args));
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.functionWithArguments =
					new PredicateWithArgumentsComposite(predicateWithArgumentsComposite.function,
							translateArgs(predicateWithArgumentsComposite.args));

		}

		private FunctionWithArguments[] translateArgs(final FunctionWithArguments[] originalArgs) {
			final int numArgs = originalArgs.length;
			final FunctionWithArguments[] translatedArgs = new FunctionWithArguments[numArgs];
			for (int i = 0; i < numArgs; ++i) {
				final FunctionWithArguments originalArg = originalArgs[i];
				translatedArgs[i] =
						originalArg.accept(new FunctionWithArgumentsTranslator(this.addresses))
								.getFunctionWithArguments();
			}
			return translatedArgs;
		}

		@Override
		public void visit(final PredicateWithArgumentsMockup predicateWithArgumentsMockup) {
			this.functionWithArguments =
					new PredicateWithArgumentsMockup(predicateWithArgumentsMockup.isReturnValue(),
							predicateWithArgumentsMockup.getPaths());
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
			this.functionWithArguments = new ParameterLeaf(pathLeaf.getReturnType());
		}

	}
}
