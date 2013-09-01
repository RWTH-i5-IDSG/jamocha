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

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.NetworkAddress;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
	/**
	 * Contains Predicates in an ordered list, which is processed from front to
	 * back. Note: Hierarchy doesn't enforce the filterSteps to be Predicates,
	 * ctor needs to do this
	 */
	FilterElement filterSteps[];

	@Getter
	@RequiredArgsConstructor
	public static class FilterElement {
		final FunctionWithArguments function;
		final NetworkAddress addressesInTarget[];
	}

	public Filter(final FunctionWithArguments[] predicates) {
		final int length = predicates.length;
		this.filterSteps = new FilterElement[length];
		for (int i = 0; i < length; ++i) {
			final FunctionWithArguments predicate = predicates[i];
			if (predicate.returnType() != SlotType.BOOLEAN) {
				throw new IllegalArgumentException(
						"The top-level FunctionWithArguments of a Filter have to be predicates!");
			}
			this.filterSteps[i] = new FilterElement(predicate, null);
		}
	}

	public Set<Path> gatherPaths() {
		final Set<Path> paths = new HashSet<>();
		for (final FilterElement step : filterSteps) {
			step.function.gatherPaths(paths);
		}
		return paths;
	}

}
