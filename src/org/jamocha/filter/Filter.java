/*
 * Copyright 2002-2013 The Jamocha Team
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.nodes.Node;

/**
 * A Filter contains {@link FilterElement filter elements} representing atomic {@link Predicate
 * predicates} specifying the tests to be performed on data according to the condition part of a
 * rule. The order of the {@link FilterElement filter elements} stored dictates the order in which
 * the affected {@link Node nodes} are joined.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see FilterElement
 * @see FunctionWithArguments
 * @see Predicate
 * @see Node
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Filter<FE extends Filter.FilterElement> {
	/**
	 * Contains Predicates in an ordered list, which is processed from front to back.
	 */
	@NonNull
	final FE filterElements[];

	@Getter
	@RequiredArgsConstructor
	public static abstract class FilterElement {
		final PredicateWithArguments function;

		public boolean equalsInFunction(final FilterElement filterElement) {
			return function.equalsInFunction(filterElement.function);
		}
	}

	// TODO make this filter order and structure independent
	public boolean equalsInFunction(final Filter<? extends Filter.FilterElement> filter) {
		if (this.filterElements.length != filter.filterElements.length)
			return false;
		for (int i = 0; i < this.filterElements.length; i++) {
			if (!this.filterElements[i].equalsInFunction(filter.filterElements[i]))
				return false;
		}
		return true;
	}

}
