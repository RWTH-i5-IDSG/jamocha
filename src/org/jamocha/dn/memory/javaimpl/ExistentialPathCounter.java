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

import java.util.LinkedList;
import java.util.List;

import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;

/**
 * Visitor for {@link org.jamocha.filter.Filter.FilterElement FilterElements} to determine how many
 * of them are existential filter elements and whether they are negated.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ExistentialPathCounter {

	public static boolean[] getNegatedArrayFromFilter(final PathFilter filter) {
		final List<Boolean> negated = new LinkedList<>();
		for (final Path path : filter.getPositiveExistentialPaths()) {
			negated.add(false);
		}
		for (final Path path : filter.getPositiveExistentialPaths()) {
			negated.add(true);
		}
		final int size = negated.size();
		final boolean[] array = new boolean[size];
		for (int i = 0; i < size; ++i) {
			array[i] = negated.get(i);
		}
		return array;
	}

}
