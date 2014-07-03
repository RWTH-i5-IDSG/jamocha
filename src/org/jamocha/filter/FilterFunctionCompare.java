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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
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
	@RequiredArgsConstructor
	static class AddressContainer {
		final AddressFilterElement addressFilterElement;
		int indexInAddresses = 0;

		final SlotInFactAddress getNextAddress() {
			return addressFilterElement.addressesInTarget[indexInAddresses++];
		}
	}

	final AddressContainer targetAddressContainer;
	final AddressContainer compareAddressContainer;
	boolean equal = true;

	private FilterFunctionCompare(final AddressFilterElement targetFilterElement,
			final AddressFilterElement compareFilterElement) {
		super();
		this.targetAddressContainer = new AddressContainer(targetFilterElement);
		this.compareAddressContainer = new AddressContainer(compareFilterElement);
		targetFilterElement.getFunction().accept(
				new FunctionTypeIdentificationVisitor(compareFilterElement.getFunction()));
	}

	private void invalidate() {
		this.equal = false;
	}

	private boolean isValid() {
		return this.equal;
	}

	private abstract class SelectiveFWAVisitor implements FunctionWithArgumentsVisitor {
		@Override
		public void visit(ConstantLeaf constantLeaf) {
			invalidate();
		}

		@Override
		public void visit(ParameterLeaf parameterLeaf) {
			invalidate();
		}

		@Override
		public void visit(PathLeaf pathLeaf) {
			invalidate();
		}

		@Override
		public void visit(PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			invalidate();
		}

		@Override
		public void visit(FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			invalidate();
		}

		public void invalidate() {
			FilterFunctionCompare.this.invalidate();
		}
	}

	private class FunctionTypeIdentificationVisitor extends SelectiveFWAVisitor {
		final FunctionWithArguments fwa;

		private FunctionTypeIdentificationVisitor(final FunctionWithArguments fwa) {
			this.fwa = fwa;
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			this.fwa.accept(new ParameterLeafVisitor(parameterLeaf));
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			this.fwa.accept(new CompositeVisitor(predicateWithArgumentsComposite));
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			this.fwa.accept(new CompositeVisitor(functionWithArgumentsComposite));
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			this.fwa.accept(new ConstantLeafVisitor(constantLeaf));
		}
	};

	private class ParameterLeafVisitor extends SelectiveFWAVisitor {
		final ParameterLeaf parameterLeaf;

		private ParameterLeafVisitor(final ParameterLeaf parameterLeaf) {
			this.parameterLeaf = parameterLeaf;
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
			if (this.parameterLeaf.getType() != parameterLeaf.getType()) {
				invalidate();
			}
			try {
				final SlotInFactAddress compareAddress =
						FilterFunctionCompare.this.compareAddressContainer.getNextAddress();
				final SlotInFactAddress targetAddress =
						FilterFunctionCompare.this.targetAddressContainer.getNextAddress();
				if (!compareAddress.equals(targetAddress)) {
					invalidate();
				}
			} catch (final IndexOutOfBoundsException e) {
				invalidate();
			}
		}
	};

	private class ConstantLeafVisitor extends SelectiveFWAVisitor {
		final ConstantLeaf constantLeaf;

		private ConstantLeafVisitor(final ConstantLeaf constantLeaf) {
			this.constantLeaf = constantLeaf;
		}

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
			if (!constantLeaf.getValue().equals(this.constantLeaf.getValue())) {
				invalidate();
			}
		}
	};

	private class CompositeVisitor extends SelectiveFWAVisitor {
		final GenericWithArgumentsComposite<?, ?> composite;

		private CompositeVisitor(final GenericWithArgumentsComposite<?, ?> composite) {
			this.composite = composite;
		}

		@AllArgsConstructor
		class Bool {
			boolean equal;
		}

		private void generic(final GenericWithArgumentsComposite<?, ?> genericWithArgumentsComposite) {
			if (!genericWithArgumentsComposite.getFunction().inClips()
					.equals(this.composite.getFunction().inClips())) {
				invalidate();
				return;
			}
			final FunctionWithArguments[] addressArgs = genericWithArgumentsComposite.getArgs();
			final FunctionWithArguments[] pathArgs = this.composite.getArgs();
			if (addressArgs.length != pathArgs.length) {
				invalidate();
				return;
			}
			// compare args normally
			compareArguments(addressArgs, pathArgs);
			// just matches
			if (FilterFunctionCompare.this.isValid())
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
										if (FilterFunctionCompare.this.isValid()) {
											// is actually equal
											bool.equal = true;
										} else {
											// lets try again
											FilterFunctionCompare.this.equal = true;
										}
									}
								});
			}
			if (!bool.equal) {
				invalidate();
			}
		}

		private void compareArguments(final FunctionWithArguments[] addressArgs,
				final FunctionWithArguments[] pathArgs) {
			for (int i = 0; i < addressArgs.length; i++) {
				final FunctionWithArguments addressFWA = addressArgs[i];
				final FunctionWithArguments pathFWA = pathArgs[i];
				pathFWA.accept(new FunctionTypeIdentificationVisitor(addressFWA));
				if (!FilterFunctionCompare.this.isValid())
					return;
			}
		}

		private int gcd(int x, int y) {
			int a = x, b = y;
			while (b > 0) {
				int temp = b;
				b = a % b; // % is remainder
				a = temp;
			}
			return a;
		}

		private int lcm(int a, int b) {
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

	public static boolean equals(final AddressFilterElement targetFilterElement,
			final AddressFilterElement compareFilterElement) {
		return new FilterFunctionCompare(targetFilterElement, compareFilterElement).equal;
	}

	public static boolean equals(final Node targetNode, final PathFilter pathFilter) {
		final LinkedHashSet<Path> paths =
				PathCollector.newLinkedHashSet().collect(pathFilter).getPaths();
		final Edge[] edges = targetNode.getIncomingEdges();
		final Set<Path> joinedPaths = new HashSet<>();
		edgeloop: for (final Edge edge : edges) {
			for (final Iterator<Path> iterator = paths.iterator(); iterator.hasNext();) {
				final Path currentPath = iterator.next();
				if (currentPath.getCurrentlyLowestNode() != edge.getSourceNode()) {
					continue;
				}
				for (final Path path : currentPath.getJoinedWith()) {
					final FactAddress localizedAddress =
							edge.localizeAddress(path.getFactAddressInCurrentlyLowestNode());
					path.cachedOverride(targetNode, localizedAddress, joinedPaths);
					joinedPaths.add(path);
					paths.remove(path);
				}
				continue edgeloop;
			}
		}
		assert paths.isEmpty();
		final AddressFilter translatedFilter = FilterTranslator.translate(pathFilter, a -> null);
		final AddressFilter targetFilter = targetNode.getFilter().getNormalisedVersion();
		final boolean equal = equals(translatedFilter, targetFilter);
		for (final Path path : joinedPaths) {
			path.restoreCache();
		}
		return equal;
	}

	public static boolean equals(final AddressFilter targetFilter,
			final AddressFilter translatedFilter) {
		final AddressFilterElement[] targetFEs =
				targetFilter.getNormalisedVersion().getFilterElements();
		final AddressFilterElement[] translatedFEs =
				translatedFilter.getNormalisedVersion().getFilterElements();
		for (int i = 0; i < targetFEs.length; i++) {
			if (!equals(targetFEs[i], translatedFEs[i])) {
				return false;
			}
		}
		return true;
	}
}
