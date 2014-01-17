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

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class PathFilter extends Filter<PathFilter.PathFilterElement> {
	public static class PathFilterElement extends Filter.FilterElement {
		public PathFilterElement(final PredicateWithArguments function) {
			super(function);
		}
	}

	/**
	 * Constructs the filter. Checks that the given {@link FunctionWithArguments functions with
	 * arguments} contain {@link Predicate predicates} on the top-level.
	 * 
	 * @param predicates
	 *            predicates to be used in the filter
	 */
	public PathFilter(final PredicateWithArguments... predicates) {
		super(wrapPredicates(predicates));
	}

	private static PathFilterElement[] wrapPredicates(final PredicateWithArguments[] predicates) {
		final int length = predicates.length;
		final PathFilterElement filterElements[] = new PathFilterElement[length];
		for (int i = 0; i < length; ++i) {
			filterElements[i] = new PathFilterElement(predicates[i]);
		}
		return filterElements;
	}
}
