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

import static java.util.stream.Collectors.toSet;

import java.util.*;

import lombok.Getter;

import org.apache.commons.collections4.IteratorUtils;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.predicates.DummyPredicate;
import org.jamocha.visitor.Visitable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class PathFilter extends Filter<PathFilter.PathFilterElement> implements PathFilterList {

	@Getter(lazy = true)
	final private int hashCode = generateHashCode();

	@Getter(lazy = true)
	final private PathFilter normalizedPathFilter = normalise();
	
	public static PathFilter empty = new PathFilter(new HashSet<>(), new HashSet<>(),
			new PathFilterElement[] {});

	@Getter
	protected final Set<Path> positiveExistentialPaths, negativeExistentialPaths;

	public static class PathFilterElement extends Filter.FilterElement implements Visitable<PathFilterElementVisitor> {
		public PathFilterElement(final PredicateWithArguments function) {
			super(function);
		}

		@Override
		public <V extends PathFilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	@Getter
	public static class DummyPathFilterElement extends PathFilterElement {
		final Path[] paths;

		public DummyPathFilterElement(final Path... paths) {
			super(new PredicateWithArgumentsComposite(DummyPredicate.instance));
			this.paths = paths;
		}

		@Override
		public <V extends PathFilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * Constructs the filter using the given existential paths and filter elements.
	 *
	 * @param positiveExistentialPaths
	 * 		set of all positive existential paths that are part of the filter or have been joined to such paths
	 * @param negativeExistentialPaths
	 * 		set of all negative existential paths that are part of the filter or have been joined to such paths
	 * @param filterElements
	 * 		filter elements to be used in the filter
	 */
	public PathFilter(final Set<Path> positiveExistentialPaths, final Set<Path> negativeExistentialPaths,
			final PathFilterElement... filterElements) {
		super(filterElements);
		assert Collections.disjoint(positiveExistentialPaths, negativeExistentialPaths);
		this.positiveExistentialPaths = positiveExistentialPaths;
		this.negativeExistentialPaths = negativeExistentialPaths;
	}

	/**
	 * Constructs the filter using the given filter elements. The existential paths are treated as positive existential
	 * paths if isPositive is true and as negative existential paths if isPositive is false.
	 *
	 * @param isPositive
	 * 		whether the existential paths are positive
	 * @param existentialPaths
	 * 		set of all existential paths that are part of the filter or have been joined to such paths
	 * @param filterElements
	 * 		filter elements to be used in the filter
	 */
	public PathFilter(final boolean isPositive, final Set<Path> existentialPaths,
			final PathFilterElement... filterElements) {
		this(isPositive ? existentialPaths : new HashSet<>(), isPositive ? new HashSet<>() : existentialPaths,
				filterElements);
	}

	/**
	 * Constructs the filter using the given filter elements without any existential paths.
	 *
	 * @param filterElements
	 * 		filter elements to be used in the filter
	 */
	public PathFilter(final PathFilterElement... filterElements) {
		this(new HashSet<>(), new HashSet<>(), filterElements);
	}

	public PathFilter normalise() {
		final int numFEs = filterElements.length;
		final PathFilterElement[] normalPFEs = new PathFilterElement[numFEs];
		for (int i = 0; i < numFEs; i++) {
			final PredicateWithArguments functionToNormalise = filterElements[i].function;
			// step one: transform to uniform function symbols
			final PredicateWithArguments uniformFunction = UniformFunctionTranslator.translate(functionToNormalise);
			// step two: sort arguments
			final PredicateWithArguments normalFunction = FunctionNormaliser.normalise(uniformFunction);
			normalPFEs[i] = new PathFilterElement(normalFunction);
		}
		Arrays.sort(normalPFEs, (final PathFilterElement a, final PathFilterElement b) -> {
			return Integer.compare(a.function.hash(), b.function.hash());
		});
		return new PathFilter(positiveExistentialPaths, negativeExistentialPaths, normalPFEs);
	}
	
	private int generateHashCode() {
		return Arrays.hashCode(Arrays.stream(getNormalizedPathFilter().getFilterElements())
				.mapToInt(fe -> fe.getFunction().hash()).toArray());
	}
	
	public static boolean equals(final PathFilter filter1, final PathFilter filter2) {
		if (filter1.getHashCode() != filter2.getHashCode())
			return false;
		final FilterFunctionCompare.PathFilterCompare compare =
				new FilterFunctionCompare.PathFilterCompare(filter1,
						filter2);
		if (!compare.isEqual())
			return false;
		if (!filter1.getNegativeExistentialPaths().stream()
				.map(p -> compare.getPathMap().get(p)).collect(toSet())
				.equals(filter2.getNegativeExistentialPaths()))
			return false;
		if (!filter1.getPositiveExistentialPaths().stream()
				.map(p -> compare.getPathMap().get(p)).collect(toSet())
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
	public Iterator<PathFilter> iterator() {
		return IteratorUtils.singletonIterator(this);
	}
}
