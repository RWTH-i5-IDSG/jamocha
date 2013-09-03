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
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.SlotInFactAddress;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
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
			if (predicate.returnType() != SlotType.BOOLEAN) {
				throw new IllegalArgumentException(
						"The top-level FunctionWithArguments of a Filter have to be predicates!");
			}
		}
	}

	public Filter(final FunctionWithArguments[] predicates) {
		final int length = predicates.length;
		this.filterElements = new FilterElement[length];
		for (int i = 0; i < length; ++i) {
			final FunctionWithArguments predicate = predicates[i];
			if (predicate.returnType() != SlotType.BOOLEAN) {
				throw new IllegalArgumentException(
						"The top-level FunctionWithArguments of a Filter have to be predicates!");
			}
			this.filterElements[i] = new FilterElement(predicate);
		}
	}

	public void translatePath() {
		for (final FilterElement filterElement : this.filterElements) {
			final ArrayList<SlotInFactAddress> addressesInTarget = new ArrayList<>();
			filterElement.function.translatePath(addressesInTarget);
			filterElement.addressesInTarget = addressesInTarget
					.toArray(new SlotInFactAddress[addressesInTarget.size()]);
		}
	}

	public Set<Path> gatherPaths() {
		final Set<Path> paths = new HashSet<>();
		for (final FilterElement step : filterElements) {
			step.function.gatherPaths(paths);
		}
		return paths;
	}

}
