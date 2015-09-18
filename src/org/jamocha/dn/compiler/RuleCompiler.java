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
package org.jamocha.dn.compiler;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.ConstructCache.Defrule.PathSetRule;
import org.jamocha.dn.compiler.ecblocks.CEToECTranslator;
import org.jamocha.dn.compiler.ecblocks.ECBlocks;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ECBlockSet;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ExistentialProxy;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Rule;
import org.jamocha.dn.compiler.ecblocks.Randomizer;
import org.jamocha.dn.compiler.pathblocks.PathBlocks;
import org.jamocha.dn.compiler.pathblocks.PathFilterConsolidator;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.optimizer.Optimizer;
import org.jamocha.filter.optimizer.PathFilterOrderOptimizer;
import org.jamocha.filter.optimizer.SamePathsFilterCombiningOptimizer;
import org.jamocha.filter.optimizer.SamePathsNodeFilterSetCombiningOptimizer;
import org.jamocha.filter.optimizer.SubsetPathsNodeFilterSetCombiningOptimizer;

import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public enum RuleCompiler {
	TRIVIAL {
		@Override
		public Collection<PathRule> compileRules(final Template initialFactTemplate, final Collection<Defrule> defrules) {
			final List<PathSetRule> consolidatedRules =
					defrules.stream()
							.flatMap(
									rule -> new PathFilterConsolidator(initialFactTemplate, rule).consolidate()
											.stream()).collect(toList());
			Collection<PathRule> transformedRules =
					consolidatedRules.stream().map(PathSetRule::trivialToPathRule).collect(toList());
			for (final Optimizer optimizer : ImmutableList.of(
			/*
			 * node filter sets using the same paths can be combined
			 */
			SamePathsNodeFilterSetCombiningOptimizer.instance,
			/*
			 * filters using the same paths can be combined
			 */
			SamePathsFilterCombiningOptimizer.instance,
			/*
			 * now perform the actual optimization of the filter order
			 */
			PathFilterOrderOptimizer.instance,
			/*
			 * now that the order of the node filter sets is fixed, we can combine node filter sets
			 * using only a subset of the paths of their predecessors
			 */
			SubsetPathsNodeFilterSetCombiningOptimizer.instance)) {
				transformedRules = optimizer.optimize(transformedRules);
			}
			return transformedRules;
		}
	},
	PATHBLOCKS {
		@Override
		public Collection<PathRule> compileRules(final Template initialFactTemplate, final Collection<Defrule> defrules) {
			final List<PathSetRule> consolidatedRules =
					defrules.stream()
							.flatMap(
									rule -> new PathFilterConsolidator(initialFactTemplate, rule).consolidate()
											.stream()).collect(toList());
			Collection<PathRule> transformedRules = PathBlocks.transform(consolidatedRules);
			for (final Optimizer optimizer : ImmutableList.of(
			/*
			 * now perform the actual optimization of the filter order
			 */
			PathFilterOrderOptimizer.instance,
			/*
			 * node filter sets using the same paths can be combined
			 */
			SamePathsNodeFilterSetCombiningOptimizer.instance,
			/*
			 * filters using the same paths can be combined
			 */
			SamePathsFilterCombiningOptimizer.instance,
			/*
			 * now that the order of the node filter sets is fixed, we can combine node filter sets
			 * using only a subset of the paths of their predecessors
			 */
			SubsetPathsNodeFilterSetCombiningOptimizer.instance)) {
				transformedRules = optimizer.optimize(transformedRules);
			}
			return transformedRules;
		}
	},
	ECBLOCKS {
		@Override
		public Collection<PathRule> compileRules(final Template initialFactTemplate, final Collection<Defrule> defrules) {
			final List<ECSetRule> consolidatedRules =
					defrules.stream()
							.flatMap(rule -> new CEToECTranslator(initialFactTemplate, rule).translate().stream())
							.collect(toList());
			Collection<PathRule> transformedRules = ECBlocks.transform(consolidatedRules);
			for (final Optimizer optimizer : ImmutableList.of(
			/*
			 * node filter sets using the same paths can be combined
			 */
			SamePathsNodeFilterSetCombiningOptimizer.instance,
			/*
			 * filters using the same paths can be combined
			 */
			SamePathsFilterCombiningOptimizer.instance,
			/*
			 * node filter sets using only a subset of the paths of their predecessors can be
			 * combined
			 */
			SubsetPathsNodeFilterSetCombiningOptimizer.instance)) {
				transformedRules = optimizer.optimize(transformedRules);
			}
			return transformedRules;
		}
	},
	ECBLOCKSRAND {
		@Override
		public Collection<PathRule> compileRules(final Template initialFactTemplate, final Collection<Defrule> defrules) {
			final List<ECSetRule> consolidatedRules =
					defrules.stream()
							.flatMap(rule -> new CEToECTranslator(initialFactTemplate, rule).translate().stream())
							.collect(toList());
			final Pair<List<Either<Rule, ExistentialProxy>>, ECBlockSet> pair = ECBlocks.compile(consolidatedRules);
			final List<Either<Rule, ExistentialProxy>> rules = pair.getLeft();
			final ECBlockSet blockSet = pair.getRight();
			Collection<PathRule> transformedRules = Randomizer.randomize(rules, blockSet);
			for (final Optimizer optimizer : ImmutableList.of(
			/*
			 * node filter sets using the same paths can be combined
			 */
			SamePathsNodeFilterSetCombiningOptimizer.instance,
			/*
			 * filters using the same paths can be combined
			 */
			SamePathsFilterCombiningOptimizer.instance,
			/*
			 * node filter sets using only a subset of the paths of their predecessors can be
			 * combined
			 */
			SubsetPathsNodeFilterSetCombiningOptimizer.instance)) {
				transformedRules = optimizer.optimize(transformedRules);
			}
			return transformedRules;
		}
	},
	;
	public abstract Collection<Defrule.PathRule> compileRules(final Template initialFactTemplate,
			final Collection<Defrule> defrules);
}
