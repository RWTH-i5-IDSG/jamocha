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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import org.jamocha.filter.fwa.PredicateWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilter extends Filter<PathFilter.PathFilterElement> {
	public static PathFilter empty = new PathFilter(new HashSet<Path>(), new HashSet<Path>(),
			new PathFilterElement[] {});

	@Getter
	protected final Set<Path> positiveExistentialPaths, negativeExistentialPaths;

	public static class PathFilterElement extends Filter.FilterElement {
		public PathFilterElement(final PredicateWithArguments function) {
			super(function);
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
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathFilter(final Set<Path> positiveExistentialPaths,
			final Set<Path> negativeExistentialPaths, final PathFilterElement... filterElements) {
		super(filterElements);
		assert Collections.disjoint(positiveExistentialPaths, negativeExistentialPaths);
		this.positiveExistentialPaths = positiveExistentialPaths;
		this.negativeExistentialPaths = negativeExistentialPaths;
	}

	/**
	 * Constructs the filter using the given filter elements without any existential paths.
	 * 
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathFilter(final PathFilterElement... filterElements) {
		this(new HashSet<Path>(), new HashSet<Path>(), filterElements);
	}

	public PathFilter normalise() {
		final int numFEs = filterElements.length;
		final PathFilterElement[] normalPFEs = new PathFilterElement[numFEs];
		for (int i = 0; i < numFEs; i++) {
			final PredicateWithArguments functionToNormalise = filterElements[i].function;
			// step one: transform to uniform function symbols
			final PredicateWithArguments uniformFunction =
					UniformFunctionTranslator.translate(functionToNormalise);
			// step two: sort arguments
			final PredicateWithArguments normalFunciton =
					FunctionNormaliser.normalise(uniformFunction);
			normalPFEs[i] = new PathFilterElement(normalFunciton);
		}
		Arrays.sort(normalPFEs, (final PathFilterElement a, final PathFilterElement b) -> {
			return Integer.compare(a.function.hash(), b.function.hash());
		});
		return new PathFilter(positiveExistentialPaths, negativeExistentialPaths, normalPFEs);
	}
}
