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
package org.jamocha.filter;

import static java.util.stream.Collectors.toList;
import static org.jamocha.util.ToArray.toArray;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.predicates.And;
import org.jamocha.function.impls.predicates.DummyPredicate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class TrivialPathSetToPathListConverter implements PathFilterSetVisitor {

	PathFilterList result;

	public static PathFilterList convert(final PathFilterSet filter) {
		return filter.accept(new TrivialPathSetToPathListConverter()).result;
	}

	@Override
	public void visit(final PathExistentialSet set) {
		final Set<Path> existentialPaths = set.getExistentialPaths();
		PathFilter existentialClosure = set.getExistentialClosure();
		final Set<PathFilterSet> purePart = set.getPurePart();
		final HashSet<Path> regularPaths = PathCollector.newHashSet().collectAllInSets(purePart).getPaths();
		regularPaths.removeAll(existentialPaths);
		final HashSet<Path> closurePaths = PathCollector.newHashSet().collectAllInSets(existentialClosure).getPaths();
		closurePaths.removeAll(existentialPaths);
		regularPaths.removeAll(closurePaths);
		if (closurePaths.isEmpty()) {
			final PathLeaf[] args =
					(regularPaths.isEmpty() ? new PathLeaf[] { new PathLeaf(set.getInitialPath(), null) } : toArray(
							regularPaths.stream().map(p -> new PathLeaf(p, null)), PathLeaf[]::new));
			existentialClosure =
					new PathFilter(GenericWithArgumentsComposite.newPredicateInstance(And.inClips,
							set.existentialClosure.getFunction(), new PredicateWithArgumentsComposite<PathLeaf>(
									DummyPredicate.instance, args)));
		}
		this.result =
				new PathExistentialList(set.getInitialPath(), PathFilterList.toSimpleList(purePart.stream()
						.map(TrivialPathSetToPathListConverter::convert).collect(toList())),
						PathNodeFilterSet.newExistentialPathNodeFilterSet(!set.isPositive(), existentialPaths,
								existentialClosure));
	}

	@Override
	public void visit(final PathFilter filter) {
		this.result = PathNodeFilterSet.newRegularPathNodeFilterSet(filter);
	}

}
