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

package org.jamocha.dn.compiler.ecblocks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraph.Edge;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.*;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.occurrence.*;
import org.jamocha.dn.compiler.ecblocks.column.*;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.function.fwa.TypeLeaf;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.Lambdas.newIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class MaximalColumns {
    final HashMap<Edge<FilterOccurrenceNode, ConstantBindingNode>, FilterToConstantColumn> filterToConstant =
            new HashMap<>();
    final HashMap<Edge<FilterOccurrenceNode, FunctionalExpressionBindingNode>, FilterToFunctionalExpressionColumn>
            filterToFunctionalExpression = new HashMap<>();
    final HashMap<Edge<FilterOccurrenceNode, SlotOrFactBindingNode>, FilterToTemplateColumn> filterToTemplate =
            new HashMap<>();
    final HashMap<Edge<FunctionalExpressionOccurrenceNode, ConstantBindingNode>, FunctionalExpressionToConstantColumn>
            functionalExpressionToConstant = new HashMap<>();
    final HashMap<Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>,
            FunctionalExpressionToFunctionalExpressionColumn>
            functionalExpressionToFunctionalExpression = new HashMap<>();
    final HashMap<Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>, FunctionalExpressionToTemplateColumn>
            functionalExpressionToTemplate = new HashMap<>();
    final HashMap<Edge<ImplicitOccurrenceNode, ConstantBindingNode>, ImplicitToConstantColumn> implicitToConstant =
            new HashMap<>();
    final HashMap<Edge<ImplicitOccurrenceNode, FunctionalExpressionBindingNode>, ImplicitToFunctionalExpressionColumn>
            implicitToFunctionalExpression = new HashMap<>();
    final HashMap<Edge<ImplicitOccurrenceNode, SlotOrFactBindingNode>, ImplicitToTemplateColumn> implicitToTemplate =
            new HashMap<>();

    public Column<? extends ECOccurrenceNode, ? extends BindingNode> getColumn(
            final Edge<ECOccurrenceNode, BindingNode> edge) {
        final OccurrenceType occurrenceType = edge.getSource().getNodeType();
        final BindingType bindingType = edge.getTarget().getNodeType();
        switch (occurrenceType) {
        case IMPLICIT_OCCURRENCE:
            switch (bindingType) {
            case SLOT_OR_FACT_BINDING:
                return this.implicitToTemplate.get(edge);
            case CONSTANT_EXPRESSION:
                return this.implicitToConstant.get(edge);
            case FUNCTIONAL_EXPRESSION:
                return this.implicitToFunctionalExpression.get(edge);
            }
        case FILTER_OCCURRENCE:
            switch (bindingType) {
            case SLOT_OR_FACT_BINDING:
                return this.filterToTemplate.get(edge);
            case CONSTANT_EXPRESSION:
                return this.filterToConstant.get(edge);
            case FUNCTIONAL_EXPRESSION:
                return this.filterToFunctionalExpression.get(edge);
            }
        case FUNCTIONAL_OCCURRENCE:
            switch (bindingType) {
            case SLOT_OR_FACT_BINDING:
                return this.functionalExpressionToTemplate.get(edge);
            case CONSTANT_EXPRESSION:
                return this.functionalExpressionToConstant.get(edge);
            case FUNCTIONAL_EXPRESSION:
                return this.functionalExpressionToFunctionalExpression.get(edge);
            }
        }
        throw new IllegalStateException("UNSUPPORTED EDGE TYPE DETECTED!");
    }

    public MaximalColumns(final AssignmentGraph assignmentGraph) {
        assignmentGraph.getGraph().edgeSet().stream().collect(groupingBy(this::toInfo))
                .forEach((i, e) -> i.disperse(newIdentityHashSet(e)));
    }

    @SuppressWarnings("unchecked")
    private Info toInfo(final Edge<ECOccurrenceNode, BindingNode> edge) {
        final OccurrenceType occurrenceType = edge.getSource().getNodeType();
        final BindingType bindingType = edge.getTarget().getNodeType();
        switch (occurrenceType) {
        case IMPLICIT_OCCURRENCE:
            switch (bindingType) {
            case SLOT_OR_FACT_BINDING:
                return new ImplicitToTemplateInfo(
                        ((Edge<ImplicitOccurrenceNode, SlotOrFactBindingNode>) (Edge<?, ?>) edge));
            case CONSTANT_EXPRESSION:
                return new ImplicitToConstantInfo(
                        ((Edge<ImplicitOccurrenceNode, ConstantBindingNode>) (Edge<?, ?>) edge));
            case FUNCTIONAL_EXPRESSION:
                return new ImplicitToFEInfo(
                        ((Edge<ImplicitOccurrenceNode, FunctionalExpressionBindingNode>) (Edge<?, ?>) edge));
            }
        case FILTER_OCCURRENCE:
            switch (bindingType) {
            case SLOT_OR_FACT_BINDING:
                return new FilterToTemplateInfo(
                        ((Edge<FilterOccurrenceNode, SlotOrFactBindingNode>) (Edge<?, ?>) edge));
            case CONSTANT_EXPRESSION:
                return new FilterToConstantInfo(((Edge<FilterOccurrenceNode, ConstantBindingNode>) (Edge<?, ?>) edge));
            case FUNCTIONAL_EXPRESSION:
                return new FilterToFEInfo(
                        ((Edge<FilterOccurrenceNode, FunctionalExpressionBindingNode>) (Edge<?, ?>) edge));
            }
        case FUNCTIONAL_OCCURRENCE:
            switch (bindingType) {
            case SLOT_OR_FACT_BINDING:
                return new FEToTemplateInfo(
                        ((Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>) (Edge<?, ?>) edge));
            case CONSTANT_EXPRESSION:
                return new FEToConstantInfo(
                        ((Edge<FunctionalExpressionOccurrenceNode, ConstantBindingNode>) (Edge<?, ?>) edge));
            case FUNCTIONAL_EXPRESSION:
                return new FEToFEInfo(
                        ((Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>) (Edge<?, ?>)
                                edge));
            }
        }
        throw new IllegalArgumentException("UNSUPPORTED EDGE TYPE DETECTED!");
    }

    @Data
    abstract class Info {
        final Object occurrenceInfo;
        final Object bindingInfo;

        public abstract void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges);
    }

    static boolean implicitOccurrenceInfo(final Edge<ImplicitOccurrenceNode, ? extends BindingNode> edge) {
        return edge.getSource().getCorrespondingBindingNode() == edge.getTarget();
    }

    static Pair<ExistentialInfo.FunctionWithExistentialInfo, Integer> filterOccurrenceInfo(
            final Edge<FilterOccurrenceNode, ? extends BindingNode> edge) {
        return Pair.of(edge.getSource().getFunctionWithExistentialInfo(), edge.getSource().getParameterPosition());
    }

    static Pair<FunctionWithArguments<TypeLeaf>, Integer> feOccurrenceInfo(
            final Edge<FunctionalExpressionOccurrenceNode, ? extends BindingNode> edge) {
        return Pair.of(edge.getSource().getFunction(), edge.getSource().getParameterPosition());
    }

    static ConstantLeaf<TemplateSlotLeaf> constantBindingInfo(
            final Edge<? extends ECOccurrenceNode, ConstantBindingNode> edge) {
        return edge.getTarget().getConstant();
    }

    static FunctionWithArguments<TypeLeaf> feBindingInfo(
            final Edge<? extends ECOccurrenceNode, FunctionalExpressionBindingNode> edge) {
        return edge.getTarget().getFunction();
    }

    static FunctionWithArguments<TemplateSlotLeaf> slotOrFactBindingInfo(
            final Edge<? extends ECOccurrenceNode, SlotOrFactBindingNode> edge) {
        return edge.getTarget().getSchema();
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class FilterToConstantInfo extends Info {
        FilterToConstantInfo(final Edge<FilterOccurrenceNode, ConstantBindingNode> edge) {
            super(filterOccurrenceInfo(edge), constantBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<FilterOccurrenceNode, ConstantBindingNode>>) (Set<?>) edges,
                    FilterToConstantColumn::new, MaximalColumns.this.filterToConstant);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class FilterToFEInfo extends Info {
        FilterToFEInfo(final Edge<FilterOccurrenceNode, FunctionalExpressionBindingNode> edge) {
            super(filterOccurrenceInfo(edge), feBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<FilterOccurrenceNode, FunctionalExpressionBindingNode>>) (Set<?>) edges,
                    FilterToFunctionalExpressionColumn::new, MaximalColumns.this.filterToFunctionalExpression);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class FilterToTemplateInfo extends Info {
        FilterToTemplateInfo(final Edge<FilterOccurrenceNode, SlotOrFactBindingNode> edge) {
            super(filterOccurrenceInfo(edge), slotOrFactBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<FilterOccurrenceNode, SlotOrFactBindingNode>>) (Set<?>) edges,
                    FilterToTemplateColumn::new, MaximalColumns.this.filterToTemplate);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class FEToConstantInfo extends Info {
        FEToConstantInfo(final Edge<FunctionalExpressionOccurrenceNode, ConstantBindingNode> edge) {
            super(feOccurrenceInfo(edge), constantBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<FunctionalExpressionOccurrenceNode, ConstantBindingNode>>) (Set<?>) edges,
                    FunctionalExpressionToConstantColumn::new, MaximalColumns.this.functionalExpressionToConstant);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class FEToFEInfo extends Info {
        FEToFEInfo(final Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode> edge) {
            super(feOccurrenceInfo(edge), feBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse(
                    (Set<Edge<FunctionalExpressionOccurrenceNode, FunctionalExpressionBindingNode>>) (Set<?>) edges,
                    FunctionalExpressionToFunctionalExpressionColumn::new,
                    MaximalColumns.this.functionalExpressionToFunctionalExpression);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class FEToTemplateInfo extends Info {
        FEToTemplateInfo(final Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode> edge) {
            super(feOccurrenceInfo(edge), slotOrFactBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns
                    .disperse((Set<Edge<FunctionalExpressionOccurrenceNode, SlotOrFactBindingNode>>) (Set<?>) edges,
                            FunctionalExpressionToTemplateColumn::new,
                            MaximalColumns.this.functionalExpressionToTemplate);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class ImplicitToConstantInfo extends Info {
        ImplicitToConstantInfo(final Edge<ImplicitOccurrenceNode, ConstantBindingNode> edge) {
            super(implicitOccurrenceInfo(edge), constantBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<ImplicitOccurrenceNode, ConstantBindingNode>>) (Set<?>) edges,
                    ImplicitToConstantColumn::new, MaximalColumns.this.implicitToConstant);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class ImplicitToFEInfo extends Info {
        ImplicitToFEInfo(final Edge<ImplicitOccurrenceNode, FunctionalExpressionBindingNode> edge) {
            super(implicitOccurrenceInfo(edge), feBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<ImplicitOccurrenceNode, FunctionalExpressionBindingNode>>) (Set<?>) edges,
                    ImplicitToFunctionalExpressionColumn::new, MaximalColumns.this.implicitToFunctionalExpression);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("unchecked")
    class ImplicitToTemplateInfo extends Info {
        ImplicitToTemplateInfo(final Edge<ImplicitOccurrenceNode, SlotOrFactBindingNode> edge) {
            super(implicitOccurrenceInfo(edge), slotOrFactBindingInfo(edge));
        }

        @Override
        public void disperse(final Set<Edge<ECOccurrenceNode, BindingNode>> edges) {
            MaximalColumns.disperse((Set<Edge<ImplicitOccurrenceNode, SlotOrFactBindingNode>>) (Set<?>) edges,
                    ImplicitToTemplateColumn::new, MaximalColumns.this.implicitToTemplate);
        }
    }

    private static <O extends ECOccurrenceNode, B extends BindingNode, C extends Column<O, B>> void disperse(
            final Set<Edge<O, B>> edges, final Function<Set<Edge<O, B>>, C> ctor, final HashMap<Edge<O, B>, C> target) {
        final C column = ctor.apply(edges);
        edges.forEach(edge -> target.put(edge, column));
    }
}
