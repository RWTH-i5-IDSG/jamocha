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

import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.visitor.FilterElementVisitor;

/**
 * This class provides three FilterElement types:
 * <ul>
 * <li><b>PathFilterElement:</b> class for regular filter elements</li>
 * <li><b>ExistentialPathFilterElement:</b> class for existential filter elements, i.e. filter
 * elements using the <code>exists</code> keyword</li>
 * <li><b>NegatedExistentialPathFilterElement:</b> class for negated existential filter elements,
 * i.e. filter elements using the <code>not</code> keyword</li>
 * </ul>
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilter extends Filter<PathFilter.PathFilterElement> {
	public static class PathFilterElement extends Filter.FilterElement {
		public PathFilterElement(final PredicateWithArguments function) {
			super(function);
		}

		@Override
		public <V extends FilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	private static abstract class AbstractExistentialpathFilterElement extends PathFilterElement {
		final Path[] paths;

		public AbstractExistentialpathFilterElement(final PredicateWithArguments function,
				final Path... paths) {
			super(function);
			this.paths = paths;
		}
	}

	public static class ExistentialPathFilterElement extends AbstractExistentialpathFilterElement {
		public ExistentialPathFilterElement(final PredicateWithArguments function,
				final Path[] paths) {
			super(function, paths);
		}

		@Override
		public <V extends FilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class NegatedExistentialPathFilterElement extends
			AbstractExistentialpathFilterElement {
		public NegatedExistentialPathFilterElement(final PredicateWithArguments function,
				final Path[] paths) {
			super(function, paths);
		}

		@Override
		public <V extends FilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	/**
	 * Constructs the filter. Checks that the given {@link FunctionWithArguments functions with
	 * arguments} contain {@link Predicate predicates} on the top-level.
	 * 
	 * @param predicates
	 *            predicates to be used in the filter
	 */
	public PathFilter(final PathFilterElement... predicates) {
		super(predicates);
	}
}
