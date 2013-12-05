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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

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
public class Filter {
	/**
	 * Contains Predicates in an ordered list, which is processed from front to back.
	 */
	final FilterElement filterElements[];

	@Getter
	@RequiredArgsConstructor
	public static class FilterElement {
		final PredicateWithArguments function;
		SlotInFactAddress addressesInTarget[];

		/**
		 * For Unit-Tests only!
		 */
		@Deprecated
		public FilterElement(final PredicateWithArguments function,
				final SlotInFactAddress... addressesInTarget) {
			this.function = function;
			this.addressesInTarget = addressesInTarget;
		}

		public boolean equalsInFunction(final FilterElement filterElement) {
			return function.equalsInFunction(filterElement.function);
		}
	}

	/**
	 * For Unit-Tests only!
	 */
	@Deprecated
	public Filter(final FilterElement filterElements[]) {
		this.filterElements = filterElements;
	}

	/**
	 * Creates a shallow copy.
	 * 
	 * @param filter
	 *            filter to copy
	 */
	private Filter(final Filter filter) {
		this.filterElements = Arrays.copyOf(filter.filterElements, filter.filterElements.length);
	}

	/**
	 * Constructs the filter. Checks that the given {@link FunctionWithArguments functions with
	 * arguments} contain {@link Predicate predicates} on the top-level.
	 * 
	 * @param predicates
	 *            predicates to be used in the filter
	 */
	public Filter(final PredicateWithArguments... predicates) {
		final int length = predicates.length;
		this.filterElements = new FilterElement[length];
		for (int i = 0; i < length; ++i) {
			this.filterElements[i] = new FilterElement(predicates[i]);
		}
	}

	/**
	 * Translates any {@link PathLeaf path leafs} into {@link ParameterLeaf parameter leafs} and
	 * adds their {@link SlotInFactAddress SlotInFactAddresses} to the corresponding
	 * {@link FilterElement filter element}.
	 */
	public void translatePath() {
		for (final FilterElement filterElement : this.filterElements) {
			final ArrayList<SlotInFactAddress> addressesInTarget = new ArrayList<>();
			filterElement.function.translatePath(addressesInTarget);
			filterElement.addressesInTarget =
					addressesInTarget.toArray(new SlotInFactAddress[addressesInTarget.size()]);
		}
	}

	/**
	 * Gathers the {@link Path paths} used in any {@link PathLeaf path leafs}.
	 * 
	 * @return {@link Path paths} used in any {@link PathLeaf path leafs}
	 */
	public LinkedHashSet<Path> gatherPaths() {
		return gatherPaths(new LinkedHashSet<Path>());
	}

	/**
	 * Gathers the {@link Path paths} used in any {@link PathLeaf path leafs}.
	 * 
	 * @return {@link Path paths} used in any {@link PathLeaf path leafs}
	 */
	public <T extends Collection<Path>> T gatherPaths(final T paths) {
		for (final FilterElement step : filterElements) {
			step.function.gatherPaths(paths);
		}
		return paths;
	}

	/**
	 * Counts the number of paths that originally went into this Filter for assertion purposes.
	 * 
	 * @return the number of paths that originally went into this Filter
	 */
	public int countParameters() {
		final Set<FactAddress> parameters = new HashSet<>();
		for (final FilterElement filterElement : this.filterElements) {
			for (final SlotInFactAddress slotInFactAddress : filterElement.addressesInTarget) {
				parameters.add(slotInFactAddress.getFactAddress());
			}
		}
		return parameters.size();
	}

	public boolean equalsInFunction(final Filter filter) { // TODO make this filter order and
															// structure independent
		if (this.filterElements.length != filter.filterElements.length)
			return false;
		for (int i = 0; i < this.filterElements.length; i++) {
			if (!this.filterElements[i].equalsInFunction(filter.filterElements[i]))
				return false;
		}
		return true;
	}

	/**
	 * Creates a shallow copy.
	 * 
	 * @return a shallow copy
	 */
	public Filter shallowCopy() {
		return new Filter(this);
	}

}
