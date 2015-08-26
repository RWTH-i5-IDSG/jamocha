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

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.ConstructCache.Defrule.PathSetBasedRule;
import org.jamocha.dn.compiler.ecblocks.CEToECTranslator;
import org.jamocha.dn.compiler.ecblocks.ECBlocks;
import org.jamocha.dn.compiler.simpleblocks.PathFilterConsolidator;
import org.jamocha.dn.compiler.simpleblocks.SimpleBlocks;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.optimizer.Optimizer;
import org.jamocha.filter.optimizer.PathFilterOrderOptimizer;
import org.jamocha.filter.optimizer.SamePathsFilterCombiningOptimizer;
import org.jamocha.filter.optimizer.SamePathsNodeFilterSetCombiningOptimizer;
import org.jamocha.filter.optimizer.SubsetPathsNodeFilterSetCombiningOptimizer;

import com.google.common.collect.ImmutableList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public enum RuleCompiler {
	TRIVIAL {
		@Override
		public Collection<PathRule> compileRules(final Template initialFactTemplate, final Collection<Defrule> defrules) {
			final List<PathSetBasedRule> consolidatedRules =
					defrules.stream()
							.flatMap(
									rule -> new PathFilterConsolidator(initialFactTemplate, rule).consolidate()
											.stream()).collect(toList());
			Collection<PathRule> transformedRules =
					consolidatedRules.stream().map(PathSetBasedRule::trivialToPathRule).collect(toList());
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
	SIMPLEBLOCKS {
		@Override
		public Collection<PathRule> compileRules(final Template initialFactTemplate, final Collection<Defrule> defrules) {
			final List<PathSetBasedRule> consolidatedRules =
					defrules.stream()
							.flatMap(
									rule -> new PathFilterConsolidator(initialFactTemplate, rule).consolidate()
											.stream()).collect(toList());
			Collection<PathRule> transformedRules = SimpleBlocks.transform(consolidatedRules);
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
	;
	public abstract Collection<Defrule.PathRule> compileRules(final Template initialFactTemplate,
			final Collection<Defrule> defrules);
}
