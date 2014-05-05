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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.fwa.ConstantLeaf;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.FunctionWithArgumentsComposite;
import org.jamocha.filter.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.filter.fwa.GenericWithArgumentsComposite;
import org.jamocha.filter.fwa.PathLeaf;
import org.jamocha.filter.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;

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
		this.pathFilterElement.getFunction().accept(
				new PathVisitor(this, this.addressFilterElement.getFunction()));
	}

	private void invalidate() {
		this.equal = false;
	}

	private boolean isValid() {
		return this.equal;
	}

	@RequiredArgsConstructor
	private static abstract class ContextAware implements FunctionWithArgumentsVisitor {
		final FilterFunctionCompare context;
	}

	private static class PathVisitor extends ContextAware {
		final FunctionWithArguments address;

		private PathVisitor(final FilterFunctionCompare context, final FunctionWithArguments address) {
			super(context);
			this.address = address;
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
				if (!this.context.addressFilterElement.getAddressesInTarget()[this.context.indexInAddresses++]
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

		@AllArgsConstructor
		static class Bool {
			boolean equal;
		}

		private void generic(final GenericWithArgumentsComposite<?, ?> genericWithArgumentsComposite) {
			if (!genericWithArgumentsComposite.getFunction().inClips()
					.equals(this.composite.getFunction().inClips())) {
				this.context.invalidate();
				return;
			}
			final FunctionWithArguments[] addressArgs = genericWithArgumentsComposite.getArgs();
			final FunctionWithArguments[] pathArgs = this.composite.getArgs();
			if (addressArgs.length != pathArgs.length) {
				this.context.invalidate();
				return;
			}
			// compare args normally
			compareArguments(addressArgs, pathArgs);
			// just matches
			if (this.context.isValid())
				return;
			// doesn't match, only has a chance if function is commutative
			if (!(genericWithArgumentsComposite.getFunction() instanceof CommutativeFunction<?>)) {
				return;
			}
			// try permutations
			final Map<Integer, List<FunctionWithArguments>> duplicates =
					Arrays.stream(pathArgs).collect(
							Collectors.groupingBy(FunctionWithArguments::hash));
			if (!duplicates.values().stream().anyMatch((v) -> {
				return v.size() > 1;
			})) {
				return;
			}
			final int lcm = duplicates.values().stream().mapToInt((a) -> {
				return a.size();
			}).reduce((a, b) -> {
				return lcm(a, b);
			}).getAsInt();
			final HashMap<FunctionWithArguments, Integer> indices =
					IntStream.range(0, pathArgs.length).collect(
							HashMap::new,
							(final HashMap<FunctionWithArguments, Integer> m, final int i) -> {
								m.put(pathArgs[i], Integer.valueOf(i));
							},
							(final HashMap<FunctionWithArguments, Integer> m,
									final HashMap<FunctionWithArguments, Integer> n) -> {
								m.putAll(n);
							});
			final Bool bool = new Bool(false);
			for (int i = 0; i < lcm; ++i) {
				final int permutation = i;
				duplicates
						.values()
						.stream()
						.filter((v) -> {
							return v.size() > 1;
						})
						.forEach(
								(final List<FunctionWithArguments> v) -> {
									final int size = v.size();
									for (int j = 0; j < size; ++j) {
										pathArgs[indices.get(v.get(j))] =
												pathArgs[indices.get(v
														.get((j + permutation) % size))];
										if (!bool.equal) {
											// equality not yet found to be true
											compareArguments(addressArgs, pathArgs);
										}
										// else just permute back to original order
										if (this.context.isValid()) {
											// is actually equal
											bool.equal = true;
										} else {
											// lets try again
											this.context.equal = true;
										}
									}
								});
			}
			if (!bool.equal) {
				this.context.invalidate();
			}
		}

		private void compareArguments(final FunctionWithArguments[] addressArgs,
				final FunctionWithArguments[] pathArgs) {
			for (int i = 0; i < addressArgs.length; i++) {
				final FunctionWithArguments addressFWA = addressArgs[i];
				final FunctionWithArguments pathFWA = pathArgs[i];
				pathFWA.accept(new PathVisitor(this.context, addressFWA));
				if (!this.context.isValid())
					return;
			}
		}

		private static int gcd(int a, int b) {
			while (b > 0) {
				int temp = b;
				b = a % b; // % is remainder
				a = temp;
			}
			return a;
		}

		private static int lcm(int a, int b) {
			return a * (b / gcd(a, b));
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
		final PathFilterElement[] pathFilterElements = pathFilter.getFilterElements();
		final AddressFilterElement[] addressFilterElements = addressFilter.getFilterElements();
		for (int i = 0; i < addressFilterElements.length; i++) {
			final PathFilterElement pathFilterElement = pathFilterElements[i];
			final AddressFilterElement addressFilterElement = addressFilterElements[i];
			if (!equals(pathFilterElement, addressFilterElement)) {
				return false;
			}
		}
		return true;
	}
}
