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
package org.jamocha.dn.memory.javaimpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.jamocha.dn.memory.CounterColumn;
import org.jamocha.dn.memory.PathFilterElementToCounterColumn;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;

/**
 * Visitor for {@link org.jamocha.filter.Filter.FilterElement FilterElements} to determine how many
 * of them are existential filter elements and whether they are negated.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
/*
 * Assumption: every existentially quantified path/address is only used in a single filter element.
 */
public class ExistentialPathCounter {
	public static boolean[] getNegatedArrayFromFilter(final PathFilter filter,
			final PathFilterElementToCounterColumn filterElementToCounterColumn) {
		final LinkedHashSet<Path> negativeExistentialPaths = filter.getNegativeExistentialPaths();
		final LinkedHashSet<Path> positiveExistentialPaths = filter.getPositiveExistentialPaths();
		final PathFilterElement[] filterElements = filter.getFilterElements();
		final int upperBound = filterElements.length;
		final boolean[] negated = new boolean[upperBound];
		int size = 0;
		for (final PathFilterElement filterElement : filterElements) {
			final CounterColumn counterColumn =
					filterElementToCounterColumn.getCounterColumn(filterElement);
			if (null == counterColumn)
				continue;
			final HashSet<Path> paths =
					PathCollector.newHashSet().collect(filterElement).getPaths();
			if (!Collections.disjoint(negativeExistentialPaths, paths)) {
				negated[size++] = false;
			} else {
				assert !Collections.disjoint(positiveExistentialPaths, paths);
				negated[size++] = true;
			}
		}
		return Arrays.copyOf(negated, size);
	}
}
