/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.filter;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

/**
 * For a documentation of the classes used here and their interaction refer to
 * {@link FunctionWithArguments}
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class Filter {
	/**
	 * Contains Predicates in an ordered list, which is processed from front to
	 * back. Note: Hierarchy doesn't enforce the filterSteps to be Predicates,
	 * ctor needs to do this
	 */
	final FilterElement filterElements[];

	@Getter
	@RequiredArgsConstructor
	public static class FilterElement {
		final FunctionWithArguments function;
		SlotInFactAddress addressesInTarget[];

		/**
		 * For Unit-Tests only!
		 */
		@Deprecated
		public FilterElement(final FunctionWithArguments function,
				final SlotInFactAddress... addressesInTarget) {
			this.function = function;
			this.addressesInTarget = addressesInTarget;
		}
	}

	/**
	 * For Unit-Tests only!
	 */
	@Deprecated
	public Filter(final FilterElement filterElements[]) {
		this.filterElements = filterElements;
		for (final FilterElement filterElement : filterElements) {
			final FunctionWithArguments predicate = filterElement.getFunction();
			if (predicate.getReturnType() != SlotType.BOOLEAN) {
				throw new IllegalArgumentException(
						"The top-level FunctionWithArguments of a Filter have to be predicates!");
			}
		}
	}

	/**
	 * Constructs the filter. Checks that the given
	 * {@link FunctionWithArguments} contain {@link Predicate}s on the
	 * top-level.
	 * 
	 * @param predicates
	 *            Predicates to be used in the filter.
	 */
	public Filter(final FunctionWithArguments[] predicates) {
		final int length = predicates.length;
		this.filterElements = new FilterElement[length];
		for (int i = 0; i < length; ++i) {
			final FunctionWithArguments predicate = predicates[i];
			if (predicate.getReturnType() != SlotType.BOOLEAN) {
				throw new IllegalArgumentException(
						"The top-level FunctionWithArguments of a Filter have to be predicates!");
			}
			this.filterElements[i] = new FilterElement(predicate);
		}
	}

	/**
	 * Translates any {@link PathLeaf}s into {@link ParameterLeaf}s and adds
	 * their {@link SlotInFactAddress}es to the corresponding
	 * {@link FilterElement}.
	 */
	public void translatePath() {
		for (final FilterElement filterElement : this.filterElements) {
			final ArrayList<SlotInFactAddress> addressesInTarget = new ArrayList<>();
			filterElement.function.translatePath(addressesInTarget);
			filterElement.addressesInTarget = addressesInTarget
					.toArray(new SlotInFactAddress[addressesInTarget.size()]);
		}
	}

	/**
	 * Gathers the {@link Path}s used in any {@link PathLeaf}s.
	 * 
	 * @return {@link Path}s used in any {@link PathLeaf}s
	 */
	public LinkedHashSet<Path> gatherPaths() {
		final LinkedHashSet<Path> paths = new LinkedHashSet<>();
		for (final FilterElement step : filterElements) {
			step.function.gatherPaths(paths);
		}
		return paths;
	}

}
