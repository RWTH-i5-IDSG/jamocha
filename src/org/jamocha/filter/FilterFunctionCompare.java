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

import lombok.RequiredArgsConstructor;

import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

import test.jamocha.filter.PredicateWithArgumentsMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class FilterFunctionCompare {
	final PathFilterElement pathFilterElement;
	final AddressFilterElement addressFilterElement;
	int indexInAddresses = 0;
	boolean equal = true;

	private FilterFunctionCompare(final PathFilterElement pathFilterElement,
			final AddressFilterElement addressFilterElement) {
		super();
		this.pathFilterElement = pathFilterElement;
		this.addressFilterElement = addressFilterElement;
		this.pathFilterElement.function.accept(new PathVisitor(this,
				this.addressFilterElement.function));
	}

	private void invalidate() {
		this.equal = false;
	}

	private boolean isValid() {
		return this.equal;
	}

	@RequiredArgsConstructor
	private static abstract class ContextAware implements Visitor {
		final FilterFunctionCompare context;
	}

	private static class PathVisitor extends ContextAware {
		final FunctionWithArguments address;

		private PathVisitor(final FilterFunctionCompare context, final FunctionWithArguments address) {
			super(context);
			this.address = address;
		}

		@Override
		public void visit(final PredicateWithArgumentsMockup predicateWithArgumentsMockup) {
			this.context.invalidate();
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
			this.address.accept(new ParameterLeafVisitor(this.context, pathLeaf));
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			this.context.invalidate();
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.address
					.accept(new CompositeVisitor(this.context, predicateWithArgumentsComposite));
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			this.address.accept(new CompositeVisitor(this.context, functionWithArgumentsComposite));
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			this.address.accept(new ConstantLeafVisitor(this.context, constantLeaf));
		}
	};

	static abstract class AddressPartVisitor extends ContextAware {
		private AddressPartVisitor(final FilterFunctionCompare context) {
			super(context);
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			this.context.invalidate();
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			this.context.invalidate();
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			this.context.invalidate();
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
			this.context.invalidate();
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.context.invalidate();
		}

		@Override
		public void visit(final PredicateWithArgumentsMockup predicateWithArgumentsMockup) {
			this.context.invalidate();
		}
	}

	static class ParameterLeafVisitor extends AddressPartVisitor {
		final PathLeaf pathLeaf;

		private ParameterLeafVisitor(final FilterFunctionCompare context, final PathLeaf pathLeaf) {
			super(context);
			this.pathLeaf = pathLeaf;
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			try {
				if (!this.context.addressFilterElement.addressesInTarget[this.context.indexInAddresses++]
						.getSlotAddress().equals(this.pathLeaf.getSlot())) {
					this.context.invalidate();
				}
			} catch (final IndexOutOfBoundsException e) {
				this.context.invalidate();
			}
		}
	};

	static class ConstantLeafVisitor extends AddressPartVisitor {
		final ConstantLeaf constantLeaf;

		private ConstantLeafVisitor(final FilterFunctionCompare context,
				final ConstantLeaf constantLeaf) {
			super(context);
			this.constantLeaf = constantLeaf;
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			if (!constantLeaf.getValue().equals(this.constantLeaf.getValue())) {
				this.context.invalidate();
			}
		}
	};

	static class CompositeVisitor extends AddressPartVisitor {
		final GenericWithArgumentsComposite<?, ?> composite;

		private CompositeVisitor(final FilterFunctionCompare context,
				final GenericWithArgumentsComposite<?, ?> constantLeaf) {
			super(context);
			this.composite = constantLeaf;
		}

		private void generic(final GenericWithArgumentsComposite<?, ?> genericWithArgumentsComposite) {
			if (!genericWithArgumentsComposite.function.toString().equals(
					composite.function.toString())) {
				this.context.invalidate();
				return;
			}
			final FunctionWithArguments[] addressArgs = genericWithArgumentsComposite.args;
			final FunctionWithArguments[] pathArgs = this.composite.args;
			if (addressArgs.length != pathArgs.length) {
				this.context.invalidate();
				return;
			}
			for (int i = 0; i < addressArgs.length; i++) {
				final FunctionWithArguments addressFWA = addressArgs[i];
				final FunctionWithArguments pathFWA = pathArgs[i];
				pathFWA.accept(new PathVisitor(this.context, addressFWA));
				if (!this.context.isValid())
					return;
			}
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			generic(functionWithArgumentsComposite);
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			generic(predicateWithArgumentsComposite);
		}
	};

	public static boolean equals(final PathFilterElement pathFilterElement,
			final AddressFilterElement addressFilterElement) {
		return new FilterFunctionCompare(pathFilterElement, addressFilterElement).equal;
	}

	public static boolean equals(final PathFilter pathFilter, final AddressFilter addressFilter) {
		for (int i = 0; i < addressFilter.filterElements.length; i++) {
			final PathFilterElement pathFilterElement = pathFilter.filterElements[i];
			final AddressFilterElement addressFilterElement = addressFilter.filterElements[i];
			if (!equals(pathFilterElement, addressFilterElement)) {
				return false;
			}
		}
		return true;
	}
}