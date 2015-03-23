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

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.IteratorUtils;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.predicates.DummyPredicate;
import org.jamocha.visitor.Visitable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class PathNodeFilterSet extends NodeFilterSet<PathLeaf, PathNodeFilterSet.PathFilter> implements PathFilterList {

	@Getter(lazy = true)
	final private int hashCode = generateHashCode();

	@Getter(lazy = true)
	final private PathNodeFilterSet normalizedPathFilter = normalise();

	public static PathNodeFilterSet empty = new PathNodeFilterSet(new HashSet<>(), new HashSet<>(), new HashSet<>());

	@Getter
	protected final Set<Path> positiveExistentialPaths, negativeExistentialPaths;

	public static class PathFilter extends NodeFilterSet.Filter<PathLeaf> implements Visitable<PathFilterVisitor>,
			Comparable<PathFilter> {
		public PathFilter(final PredicateWithArguments<PathLeaf> function) {
			super(function);
		}

		@Override
		public <V extends PathFilterVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public int compareTo(final PathFilter o) {
			return Integer.compare(this.function.hash(), o.function.hash());
		}
	}

	@Getter
	public static class DummyPathFilter extends PathFilter {
		final Path[] paths;

		public DummyPathFilter(final Path... paths) {
			super(new PredicateWithArgumentsComposite<PathLeaf>(DummyPredicate.instance));
			this.paths = paths;
		}

		@Override
		public <V extends PathFilterVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * Constructs the filter using the given existential paths and filter elements.
	 *
	 * @param positiveExistentialPaths
	 *            set of all positive existential paths that are part of the filter or have been
	 *            joined to such paths
	 * @param negativeExistentialPaths
	 *            set of all negative existential paths that are part of the filter or have been
	 *            joined to such paths
	 * @param filters
	 *            filter elements to be used in the filter
	 */
	public PathNodeFilterSet(final Set<Path> positiveExistentialPaths, final Set<Path> negativeExistentialPaths,
			final Set<PathFilter> filters) {
		super(filters);
		assert Collections.disjoint(positiveExistentialPaths, negativeExistentialPaths);
		this.positiveExistentialPaths = positiveExistentialPaths;
		this.negativeExistentialPaths = negativeExistentialPaths;
	}

	/**
	 * Constructs the filter using the given filter elements. The existential paths are treated as
	 * positive existential paths if isPositive is true and as negative existential paths if
	 * isPositive is false.
	 *
	 * @param isPositive
	 *            whether the existential paths are positive
	 * @param existentialPaths
	 *            set of all existential paths that are part of the filter or have been joined to
	 *            such paths
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathNodeFilterSet(final boolean isPositive, final Set<Path> existentialPaths, final Set<PathFilter> filters) {
		this(isPositive ? existentialPaths : new HashSet<>(), isPositive ? new HashSet<>() : existentialPaths, filters);
	}

	/**
	 * Constructs the filter using the given filter elements without any existential paths.
	 *
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathNodeFilterSet(final Set<PathFilter> filters) {
		this(new HashSet<>(), new HashSet<>(), filters);
	}

	/**
	 * Constructs the filter using the given filter elements without any existential paths.
	 *
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathNodeFilterSet(final PathFilter... filters) {
		this(new HashSet<>(Arrays.asList(filters)));
	}

	/**
	 * Constructs the filter using the given filter elements without any existential paths.
	 *
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathNodeFilterSet(final Set<Path> positiveExistentialPaths, final Set<Path> negativeExistentialPaths,
			final PathFilter... filters) {
		this(positiveExistentialPaths, negativeExistentialPaths, new HashSet<>(Arrays.asList(filters)));
	}

	@RequiredArgsConstructor
	static class PathFilterReCreator implements PathFilterVisitor {
		final PredicateWithArguments<PathLeaf> normalFunction;
		PathFilter result;

		@Override
		public void visit(final PathFilter f) {
			this.result = new PathFilter(normalFunction);
		}

		@Override
		public void visit(final DummyPathFilter f) {
			this.result = new DummyPathFilter(f.getPaths());
		}
	}

	public PathNodeFilterSet normalise() {
		return new PathNodeFilterSet(positiveExistentialPaths, negativeExistentialPaths, filters
				.stream()
				.map(filter -> {
					final PredicateWithArguments<PathLeaf> functionToNormalise = filter.function;
					// step one: transform to uniform function symbols
					final PredicateWithArguments<PathLeaf> uniformFunction =
							UniformFunctionTranslator.translate(functionToNormalise);
					// step two: sort arguments
					final PredicateWithArguments<PathLeaf> normalFunction =
							FunctionNormaliser.normalise(uniformFunction);
					return filter.accept(new PathFilterReCreator(normalFunction)).result;
				}).sorted().collect(toCollection(LinkedHashSet::new)));
	}

	private int generateHashCode() {
		return Arrays.hashCode(getNormalizedPathFilter().getFilters().stream().mapToInt(f -> f.getFunction().hash())
				.toArray());
	}

	public static boolean equals(final PathNodeFilterSet filter1, final PathNodeFilterSet filter2) {
		return equals(filter1, filter2, new HashMap<>());
	}

	public static boolean equals(final PathNodeFilterSet filter1, final PathNodeFilterSet filter2,
			final Map<Path, Path> pathMap) {
		// FIXME and other locations to handle all possible path mappings correctly
		if (filter1.getHashCode() != filter2.getHashCode())
			return false;
		final FilterFunctionCompare.PathFilterCompare compare =
				new FilterFunctionCompare.PathFilterCompare(filter1, filter2, pathMap);
		if (!compare.isEqual())
			return false;
		if (!filter1.getNegativeExistentialPaths().stream().map(p -> pathMap.get(p)).collect(toSet())
				.equals(filter2.getNegativeExistentialPaths()))
			return false;
		if (!filter1.getPositiveExistentialPaths().stream().map(p -> pathMap.get(p)).collect(toSet())
				.equals(filter2.getPositiveExistentialPaths()))
			return false;
		return true;
	}

	@Override
	public <V extends PathFilterListVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public Iterator<PathNodeFilterSet> iterator() {
		return IteratorUtils.singletonIterator(this);
	}
}
