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

import java.util.LinkedHashSet;

import lombok.Getter;

import org.jamocha.filter.fwa.PredicateWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilter extends Filter<PathFilter.PathFilterElement> {
	public static PathFilter empty = new PathFilter(new LinkedHashSet<Path>(),
			new LinkedHashSet<Path>(), new PathFilterElement[] {});

	@Getter
	protected final LinkedHashSet<Path> positiveExistentialPaths, negativeExistentialPaths;

	public static class PathFilterElement extends Filter.FilterElement {
		public PathFilterElement(final PredicateWithArguments function) {
			super(function);
		}
	}

	/**
	 * Constructs the filter using the given existential paths and filter elements.
	 * 
	 * @param positiveExistentialPaths
	 *            positive existential paths
	 * @param negativeExistentialPaths
	 *            negative existential paths
	 * @param filterElements
	 *            filter elements to be used in the filter
	 */
	public PathFilter(final LinkedHashSet<Path> positiveExistentialPaths,
			final LinkedHashSet<Path> negativeExistentialPaths,
			final PathFilterElement... filterElements) {
		super(filterElements);
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
		this(new LinkedHashSet<Path>(), new LinkedHashSet<Path>(), filterElements);
	}
}
