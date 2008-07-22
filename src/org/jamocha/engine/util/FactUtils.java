/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Peter Lin
 * 
 * the class contains utilities for doing things like sortig the facts for
 * printing.
 */
public class FactUtils {

	private static class FactComparator implements Comparator<Fact> {

		public FactComparator() {
			super();
		}

		public int compare(final Fact lf, final Fact rf) {
			if (lf.getFactId() > rf.getFactId())
				return 1;
			else if (lf.getFactId() == rf.getFactId())
				return 0;
			else
				return -1;
		}
	}

	private static final FactComparator COMPARATOR = new FactComparator();
	private static final FactTemplateComparator TEMPLATECOMP = new FactTemplateComparator();

	private FactUtils() {
		super();
	}

	public static Fact[] sortFacts(final List<Fact> facts) {
		Fact[] sorted = new Fact[facts.size()];
		sorted = facts.toArray(sorted);
		Arrays.sort(sorted, COMPARATOR);
		return sorted;
	}

	public static Fact[] sortFactsByTemplate(final List<Fact> facts) {
		Fact[] sorted = new Fact[facts.size()];
		sorted = facts.toArray(sorted);
		Arrays.sort(sorted, TEMPLATECOMP);
		return sorted;
	}
}
