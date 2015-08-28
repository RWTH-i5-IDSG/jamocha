/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.filter.optimizer;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.filter.PathFilterListVisitor;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.impls.predicates.And;
import org.jamocha.util.ToArray;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamePathsFilterCombiningOptimizer implements Optimizer {

	public static final SamePathsFilterCombiningOptimizer instance = new SamePathsFilterCombiningOptimizer();

	@RequiredArgsConstructor
	static class Identifier implements PathFilterListVisitor {
		@Override
		public void visit(final PathNodeFilterSet filterSet) {
			final Set<PathFilter> filters = filterSet.getFilters();

			final Map<HashSet<Path>, Set<PathFilter>> map =
					filters.stream().collect(
							groupingBy(pnfs -> PathCollector.newHashSet().collect(pnfs).getPaths(), toSet()));
			for (final Set<PathFilter> set : map.values()) {
				if (set.size() == 1) {
					continue;
				}
				filters.removeAll(set);
				final PredicateWithArguments<PathLeaf>[] arguments =
						ToArray.<PredicateWithArguments<PathLeaf>> toArray(set.stream().map(PathFilter::getFunction),
								PredicateWithArguments[]::new);
				filters.add(new PathFilter(GenericWithArgumentsComposite.newPredicateInstance(And.inClips, arguments)));
			}
		}

		@Override
		public void visit(final PathExistentialList filter) {
			// recurse on pure part only
			filter.getPurePart().accept(this);
		}

		@Override
		public void visit(final PathSharedList filter) {
			// recurse
			filter.getUnmodifiableFilterListCopy().forEach(f -> f.accept(this));
		}
	}

	@Override
	public Collection<PathRule> optimize(final Collection<PathRule> rules) {
		for (final PathRule pathRule : rules) {
			pathRule.getCondition().accept(new Identifier());
		}
		return rules;
	}
}
