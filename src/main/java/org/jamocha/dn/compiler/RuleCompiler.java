/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.compiler;

import com.google.common.collect.ImmutableList;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.ConstructCache.Defrule.PathSetRule;
import org.jamocha.dn.compiler.ecblocks.CEToECTranslator;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraphToDot;
import org.jamocha.dn.compiler.pathblocks.PathBlocks;
import org.jamocha.dn.compiler.pathblocks.PathFilterConsolidator;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.optimizer.*;
import org.jamocha.languages.common.RuleConditionProcessor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public enum RuleCompiler {
    TRIVIAL {
        @Override
        public Collection<PathRule> compileRules(final Template initialFactTemplate,
                final Collection<Defrule> defrules) {
            final List<PathSetRule> consolidatedRules = defrules.stream()
                    .flatMap(rule -> new PathFilterConsolidator(initialFactTemplate, rule).consolidate().stream())
                    .collect(toList());
            Collection<PathRule> transformedRules =
                    consolidatedRules.stream().map(PathSetRule::trivialToPathRule).collect(toList());
            for (final Optimizer optimizer : ImmutableList.of(
            /*
             * node filter sets using the same paths can be combined
            */
                    SamePathsNodeFilterSetCombiningOptimizer.INSTANCE,
            /*
             * filters using the same paths can be combined
             */
                    SamePathsFilterCombiningOptimizer.INSTANCE,
            /*
             * now perform the actual optimization of the filter order
             */
                    PathFilterOrderOptimizer.INSTANCE,
            /*
             * now that the order of the node filter sets is fixed, we can combine node filter sets
             * using only a subset of the paths of their predecessors
             */
                    SubsetPathsNodeFilterSetCombiningOptimizer.INSTANCE)) {
                transformedRules = optimizer.optimize(transformedRules);
            }
            return transformedRules;
        }
    },
    PATHBLOCKS {
        @Override
        public Collection<PathRule> compileRules(final Template initialFactTemplate,
                final Collection<Defrule> defrules) {
            final List<PathSetRule> consolidatedRules =
                    defrules.stream().peek(rule -> RuleConditionProcessor.flattenInPlace(rule.getCondition())).flatMap(
                            rule -> new PathFilterConsolidator(initialFactTemplate, rule).consolidate().stream())
                            .collect(toList());
            Collection<PathRule> transformedRules = PathBlocks.transform(consolidatedRules);
            for (final Optimizer optimizer : ImmutableList.of(
            /*
             * now perform the actual optimization of the filter order
             */
                    PathFilterOrderOptimizer.INSTANCE,
            /*
             * node filter sets using the same paths can be combined
             */
                    SamePathsNodeFilterSetCombiningOptimizer.INSTANCE,
            /*
             * filters using the same paths can be combined
             */
                    SamePathsFilterCombiningOptimizer.INSTANCE,
            /*
             * now that the order of the node filter sets is fixed, we can combine node filter sets
             * using only a subset of the paths of their predecessors
             */
                    SubsetPathsNodeFilterSetCombiningOptimizer.INSTANCE)) {
                transformedRules = optimizer.optimize(transformedRules);
            }
            return transformedRules;
        }
    },
    ECBLOCKS {
        @Override
        public Collection<PathRule> compileRules(final Template initialFactTemplate,
                final Collection<Defrule> defrules) {
            final List<ECSetRule> consolidatedRules =
                    defrules.stream().flatMap(rule -> RuleConditionProcessor.flattenOutOfPlace(rule).stream())
                            .flatMap(rule -> new CEToECTranslator(initialFactTemplate, rule).translate().stream())
                            .collect(toList());
            Collection<PathRule> transformedRules = ECBlocks.transform(consolidatedRules);

            for (final Optimizer optimizer : ImmutableList.of(
            /*
             * node filter sets using the same paths can be combined
             */
            SamePathsNodeFilterSetCombiningOptimizer.INSTANCE,
            /*
             * filters using the same paths can be combined
             */
            SamePathsFilterCombiningOptimizer.INSTANCE,
            /*
             * node filter sets using only a subset of the paths of their predecessors can be
             * combined
             */
            SubsetPathsNodeFilterSetCombiningOptimizer.INSTANCE)) {
            transformedRules = optimizer.optimize(transformedRules);
            }
            return transformedRules;
        }
    };

    public abstract Collection<Defrule.PathRule> compileRules(final Template initialFactTemplate,
            final Collection<Defrule> defrules);
}
