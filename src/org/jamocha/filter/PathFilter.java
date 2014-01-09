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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

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
		PathFilterElement filterElements[] = new PathFilterElement[length];
		for (int i = 0; i < length; ++i) {
			filterElements[i] = new PathFilterElement(predicates[i]);
		}
		return filterElements;
	}

	/**
	 * Translates any {@link PathLeaf path leafs} into {@link ParameterLeaf parameter leafs} and
	 * adds their {@link SlotInFactAddress SlotInFactAddresses} to the corresponding
	 * {@link FilterElement filter element}.
	 */
	public AddressFilter translatePath() {
		final int length = this.filterElements.length;
		final AddressFilterElement[] addressFilterElements = new AddressFilterElement[length];
		for (int i = 0; i < length; ++i) {
			final FilterElement originalFilterElement = this.filterElements[i];
			final ArrayList<SlotInFactAddress> addressesInTarget = new ArrayList<>();
			final PredicateWithArguments translated =
					originalFilterElement.function.translatePath(addressesInTarget);
			addressFilterElements[i] =
					new AddressFilterElement(translated,
							addressesInTarget.toArray(new SlotInFactAddress[addressesInTarget
									.size()]));
			;
		}
		return new AddressFilter(addressFilterElements);
	}

	/**
	 * Gathers the {@link Path paths} used in any {@link PathLeaf path leafs}.
	 * 
	 * @return {@link Path paths} used in any {@link PathLeaf path leafs}
	 */
	public SlotInFactAddress[] gatherCurrentAddreses() {
		final Collection<SlotInFactAddress> gatherPaths =
				gatherCurrentAddresses(new LinkedList<SlotInFactAddress>());
		return gatherPaths.toArray(new SlotInFactAddress[gatherPaths.size()]);
	}

	/**
	 * Gathers the {@link SlotInFactAddress SlotInFactAddresses} used in any {@link PathLeaf path
	 * leafs}.
	 * 
	 * @return {@link SlotInFactAddress SlotInFactAddresses} used in any {@link PathLeaf path leafs}
	 */
	public <T extends Collection<SlotInFactAddress>> T gatherCurrentAddresses(final T paths) {
		for (final FilterElement step : filterElements) {
			step.function.gatherCurrentAddresses(paths);
		}
		return paths;
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

}
