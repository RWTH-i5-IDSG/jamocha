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

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.apache.commons.lang3.ArrayUtils;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.PathFilter.DummyPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.function.CommutativeFunction;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;

/**
 * Compares the Filters.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class FilterFunctionCompare {

	abstract FunctionTypeIdentificationVisitor newFunctionTypeIdentificationVisitor(
			final FunctionWithArguments fwa);

	private static class AddressFilterFunctionCompare extends FilterFunctionCompare {
		final AddressContainer targetAddressContainer;
		final AddressContainer compareAddressContainer;

		private AddressFilterFunctionCompare(final AddressFilterElement targetFilterElement,
				final AddressFilterElement compareFilterElement) {

			super();
			this.targetAddressContainer = new AddressContainer(targetFilterElement);
			this.compareAddressContainer = new AddressContainer(compareFilterElement);
			targetFilterElement.getFunction().accept(
					newFunctionTypeIdentificationVisitor(compareFilterElement.getFunction()));
		}

		@Override
		FunctionTypeIdentificationVisitor newFunctionTypeIdentificationVisitor(
				final FunctionWithArguments fwa) {
			return new AddressFunctionTypeIdentificationVisitor(fwa);
		};

		private class AddressFunctionTypeIdentificationVisitor extends
				FunctionTypeIdentificationVisitor {

			private AddressFunctionTypeIdentificationVisitor(final FunctionWithArguments fwa) {
				super(fwa);
			}

			@Override
			public void visit(final ParameterLeaf parameterLeaf) {
				this.fwa.accept(new ParameterLeafVisitor(parameterLeaf));
			}
		}

		private class ParameterLeafVisitor extends InvalidatingFWAVisitor {
			final ParameterLeaf compareParameterLeaf;

			private ParameterLeafVisitor(final ParameterLeaf parameterLeaf) {
				this.compareParameterLeaf = parameterLeaf;
			}

			@Override
			public void visit(final ParameterLeaf targetParameterLeaf) {
				if (this.compareParameterLeaf.getType() != targetParameterLeaf.getType()) {
					invalidate();
				}
				try {
					final SlotInFactAddress compareAddress =
							AddressFilterFunctionCompare.this.compareAddressContainer
									.getNextAddress();
					final SlotInFactAddress targetAddress =
							AddressFilterFunctionCompare.this.targetAddressContainer
									.getNextAddress();
					if (!compareAddress.equals(targetAddress)) {
						invalidate();
					}
				} catch (final IndexOutOfBoundsException e) {
					invalidate();
				}
			}
		};
	}

	public static class PathFilterCompare {

		@Getter
		private final Map<Path, Path> pathMap = new HashMap<>();
		
		@Getter
		boolean equal = true;

		private boolean comparePaths(Path comparePath, Path targetPath) {
			final Path mappedPath = pathMap.get(comparePath);
			if (null != mappedPath) {
				return mappedPath == targetPath;
			}
			if (comparePath.template != targetPath.template) {
				return false;
			}
			pathMap.put(comparePath, targetPath);
			return true;
		}

		public PathFilterCompare(final PathFilter targetFilter, final PathFilter compareFilter) {
			super();
			final PathFilterElement[] targetFEs = targetFilter.normalise().getFilterElements();
			final PathFilterElement[] compareFEs = compareFilter.normalise().getFilterElements();
			if (targetFEs.length != compareFEs.length) {
				equal = false;
				return;
			}
			for (int i = 0; i < targetFEs.length; i++) {
				;
				if (!new PathFilterFirstTypeIdentificationVisitor().collect(targetFEs[i]).collect(
						compareFEs[i])) {
					equal = false;
					return;
				}
			}
		}

		private class PathFilterFirstTypeIdentificationVisitor implements PathFilterElementVisitor {

			PathFilterSecondTypeIndentificationVisitor result;

			public PathFilterSecondTypeIndentificationVisitor collect(PathFilterElement fe) {
				return fe.accept(this).result;
			}

			@Override
			public void visit(PathFilterElement fe) {
				result = new NoDummyPathFilterSecondTypeIdentificationVisitor(fe);
			}

			@Override
			public void visit(DummyPathFilterElement fe) {
				result = new DummyPathFilterSecondTypeIdentificationVisitor(fe);
			}

			private abstract class PathFilterSecondTypeIndentificationVisitor implements
					PathFilterElementVisitor {
				boolean equal = true;

				abstract public boolean collect(PathFilterElement fe);
			}

			private class NoDummyPathFilterSecondTypeIdentificationVisitor extends
					PathFilterSecondTypeIndentificationVisitor {

				final PathFilterElement targetFilterElement;

				public NoDummyPathFilterSecondTypeIdentificationVisitor(PathFilterElement fe) {
					this.targetFilterElement = fe;
				}

				@Override
				public boolean collect(PathFilterElement fe) {
					return fe.accept(this).equal;
				}

				@Override
				public void visit(PathFilterElement fe) {
					equal = new PathFilterFunctionCompare(this.targetFilterElement, fe).equal;
				}

				@Override
				public void visit(DummyPathFilterElement fe) {
					equal = false;
				}

			}

			private class DummyPathFilterSecondTypeIdentificationVisitor extends
					PathFilterSecondTypeIndentificationVisitor {

				final DummyPathFilterElement targetFilterElement;

				public DummyPathFilterSecondTypeIdentificationVisitor(DummyPathFilterElement dfe) {
					this.targetFilterElement = dfe;
				}

				@Override
				public boolean collect(PathFilterElement fe) {
					return fe.accept(this).equal;
				}

				@Override
				public void visit(PathFilterElement compareFilterElement) {
					equal = false;
				}

				@Override
				public void visit(DummyPathFilterElement compareFilterElement) {
					final Path[] targetPaths = targetFilterElement.getPaths();
					final Path[] comparePaths = compareFilterElement.getPaths();
					if (targetPaths.length != comparePaths.length) {
						equal = false;
						return;
					}
					for (int i = 0; i < targetPaths.length; i++) {
						if (!comparePaths(comparePaths[i], targetPaths[i])) {
							equal = false;
							return;
						}
					}
				}

			}

		}

		private class PathFilterFunctionCompare extends FilterFunctionCompare {

			private PathFilterFunctionCompare(final PathFilterElement targetFilterElement,
					final PathFilterElement compareFilterElement) {
				super();
				targetFilterElement.getFunction().accept(
						newFunctionTypeIdentificationVisitor(compareFilterElement.getFunction()));
			}

			@Override
			FunctionTypeIdentificationVisitor newFunctionTypeIdentificationVisitor(
					final FunctionWithArguments fwa) {
				return new PathFunctionTypeIdentificationVisitor(fwa);
			};

			private class PathFunctionTypeIdentificationVisitor extends
					FunctionTypeIdentificationVisitor {
				private PathFunctionTypeIdentificationVisitor(final FunctionWithArguments fwa) {
					super(fwa);
				}

				@Override
				public void visit(final PathLeaf pathLeaf) {
					this.fwa.accept(new PathLeafVisitor(pathLeaf));
				}
			}

			private class PathLeafVisitor extends InvalidatingFWAVisitor {
				final PathLeaf comparePathLeaf;

				private PathLeafVisitor(final PathLeaf pathLeaf) {
					this.comparePathLeaf = pathLeaf;
				}

				@Override
				public void visit(final PathLeaf targetPathLeaf) {
					if (comparePathLeaf.getSlot() != targetPathLeaf.getSlot()) {
						invalidate();
						return;
					}
					if (!comparePaths(comparePathLeaf.getPath(), targetPathLeaf.getPath()))
						invalidate();
				}
			};
		}

	}

	@RequiredArgsConstructor
	static class AddressContainer {
		final AddressFilterElement addressFilterElement;
		int indexInAddresses = 0;

		final SlotInFactAddress getNextAddress() {
			return addressFilterElement.addressesInTarget[indexInAddresses++];
		}
	}

	boolean equal = true;

	void invalidate() {
		this.equal = false;
	}

	private boolean isValid() {
		return this.equal;
	}

	private abstract class InvalidatingFWAVisitor implements DefaultFunctionWithArgumentsVisitor {
		@Override
		public void defaultAction(final FunctionWithArguments function) {
			FilterFunctionCompare.this.invalidate();
		}
	}

	private abstract class FunctionTypeIdentificationVisitor extends InvalidatingFWAVisitor {
		final FunctionWithArguments fwa;

		private FunctionTypeIdentificationVisitor(final FunctionWithArguments fwa) {
			this.fwa = fwa;
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

	private class ConstantLeafVisitor extends InvalidatingFWAVisitor {
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

	private class CompositeVisitor extends InvalidatingFWAVisitor {
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
				pathFWA.accept(newFunctionTypeIdentificationVisitor(addressFWA));
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
		return new AddressFilterFunctionCompare(targetFilterElement, compareFilterElement).equal;
	}

	static void swap(final int[] list, final int i, final int j) {
		final int tmp = list[i];
		list[i] = list[j];
		list[j] = tmp;
	}

	static class Permutation<T> {
		final List<T> list;
		final int[] p;
		final int length;

		public Permutation(final List<T> list) {
			this.length = list.size();
			this.list = list;
			this.p = new int[this.length];
			Arrays.parallelSetAll(p, i -> i);
		}

		public boolean nextPermutation() {
			if (length < 2)
				return false;
			int i = length - 1;
			while (true) {
				final int ii = i--;
				// find neighbors with p[n] < p[n+1]
				if (p[i] < p[ii]) {
					int j = length - 1;
					// find the last list[m] < p[n]
					while (p[i] >= p[j]) {
						--j;
					}
					// Swap p[n] and p[m], and reverse from m+1 to the end
					swap(p, i, j);
					ArrayUtils.reverse(p, ii, length);
					Collections.swap(list, i, j);
					Collections.reverse(list.subList(ii, length));
					return true;
				}
				// Neighbors in descending order
				// Is that true for the whole sequence?
				if (0 == i) {
					// Reverse the sequence to its original order
					ArrayUtils.reverse(p);
					Collections.reverse(list);
					return false;
				}
			}
		}
	}

	public static void main(String[] args) {
		final List<Integer> toPermute = new ArrayList<>(Arrays.asList(2, 3, 4, 5, 6, 7, 8));
		final ComponentwisePermutation<Integer> componentwisePermutation =
				new ComponentwisePermutation<>(Arrays.asList(toPermute.subList(0, 2),
						toPermute.subList(3, 7)));
		do {
			System.out.println(ArrayUtils.toString(toPermute.toArray()));
		} while (componentwisePermutation.nextPermutation());
	}

	static class ComponentwisePermutation<T> {
		final List<Permutation<T>> permutators;

		public ComponentwisePermutation(final List<List<T>> sublists) {
			this.permutators = new ArrayList<>(sublists.size());
			for (final List<T> list : sublists) {
				permutators.add(new Permutation<T>(list));
			}
		}

		public boolean nextPermutation() {
			if (this.permutators.isEmpty()) {
				return false;
			}
			for (final Iterator<Permutation<T>> iter = permutators.iterator(); iter.hasNext();) {
				final Permutation<T> element = iter.next();
				if (element.nextPermutation())
					return true;
				if (!iter.hasNext())
					return false;
			}
			return true;
		}
	}

	@Value
	static class Range {
		final int start, end;
	}

	public static Map<Path, FactAddress> equals(final Node targetNode, final PathFilter pathFilter) {
		final List<Path> pathsPermutation = new LinkedList<>();
		final ComponentwisePermutation<Path> componentwisePermutation;
		{
			final Map<FactAddress, List<Path>> pathsByNode =
					PathCollector
							.newHashSet()
							.collectAll(pathFilter)
							.getPaths()
							.stream()
							.collect(groupingBy(path -> path.getFactAddressInCurrentlyLowestNode()));
			final List<Range> ranges = new ArrayList<>(pathsByNode.size());
			for (final Entry<FactAddress, List<Path>> entry : pathsByNode.entrySet()) {
				final List<Path> newPaths = entry.getValue();
				final int start = pathsPermutation.size();
				pathsPermutation.addAll(newPaths);
				final int end = pathsPermutation.size();
				if (end - start > 1) {
					ranges.add(new Range(start, end));
				}
			}
			final List<List<Path>> sublists = new ArrayList<>(ranges.size());
			for (final Range range : ranges) {
				sublists.add(pathsPermutation.subList(range.start, range.end));
			}
			componentwisePermutation = new ComponentwisePermutation<>(sublists);
		}
		do {
			final List<Path> paths = new ArrayList<>(pathsPermutation);
			final Map<Path, FactAddress> result = new HashMap<>();
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
						result.put(path, localizedAddress);
						joinedPaths.add(path);
						paths.remove(path);
					}
					continue edgeloop;
				}
				throw new Error("For one edge no paths were found.");
			}
			assert paths.isEmpty();
			final AddressFilter translatedFilter =
					PathFilterToAddressFilterTranslator.translate(pathFilter, a -> null);
			final AddressFilter targetFilter = targetNode.getFilter();
			final boolean equal = equals(translatedFilter, targetFilter);
			for (final Path path : joinedPaths) {
				path.restoreCache();
			}
			if (equal) {
				return result;
			}
		} while (componentwisePermutation.nextPermutation());
		return null;
	}

	public static boolean equals(final AddressFilter targetFilter,
			final AddressFilter translatedFilter) {
		final AddressFilterElement[] targetFEs =
				targetFilter.getNormalisedVersion().getFilterElements();
		final AddressFilterElement[] translatedFEs =
				translatedFilter.getNormalisedVersion().getFilterElements();
		if (targetFEs.length != translatedFEs.length)
			return false;
		for (int i = 0; i < targetFEs.length; i++) {
			if (!equals(targetFEs[i], translatedFEs[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(final PathFilter targetFilter, final PathFilter compareFilter) {
		return new PathFilterCompare(targetFilter, compareFilter).equal;
	}
}
