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
package org.jamocha.filter;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.apache.commons.collections4.IteratorUtils;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public abstract class PathNodeFilterSet extends NodeFilterSet<PathLeaf, PathFilter> implements PathFilterList {

    @Getter(lazy = true)
    private final int hashCode = generateHashCode();

    @Getter(lazy = true)
    private final PathNodeFilterSet normalizedPathFilter = normalise();

    public static final PathNodeFilterSet EMPTY = new RegularPathNodeFilterSet(Collections.emptySet());

    public abstract Set<Path> getPositiveExistentialPaths();

    public abstract Set<Path> getNegativeExistentialPaths();

    public abstract boolean containsExistentials();

    protected abstract PathNodeFilterSet duplicate(final Set<PathFilter> normalisedFilters);

    public static PathNodeFilterSet newRegularPathNodeFilterSet(final Set<PathFilter> filters) {
        return new RegularPathNodeFilterSet(filters);
    }

    public static PathNodeFilterSet newRegularPathNodeFilterSet(final PathFilter... filters) {
        return newRegularPathNodeFilterSet(Sets.newHashSet(filters));
    }

    public static PathNodeFilterSet newExistentialPathNodeFilterSet(final Set<Path> positiveExistentialPaths,
            final Set<Path> negativeExistentialPaths, final Set<PathFilter> filters) {
        return new ExistentialPathNodeFilterSet(filters, positiveExistentialPaths, negativeExistentialPaths);
    }

    public static PathNodeFilterSet newExistentialPathNodeFilterSet(final Set<Path> positiveExistentialPaths,
            final Set<Path> negativeExistentialPaths, final PathFilter... filters) {
        return newExistentialPathNodeFilterSet(positiveExistentialPaths, negativeExistentialPaths,
                Sets.newHashSet(filters));
    }

    public static PathNodeFilterSet newExistentialPathNodeFilterSet(final boolean negated,
            final Set<Path> existentialPaths, final Set<PathFilter> filters) {
        return new ExistentialPathNodeFilterSet(filters, negated ? Collections.emptySet() : existentialPaths,
                negated ? existentialPaths : Collections.emptySet());
    }

    public static PathNodeFilterSet newExistentialPathNodeFilterSet(final boolean negated,
            final Set<Path> existentialPaths, final PathFilter... filters) {
        return newExistentialPathNodeFilterSet(negated, existentialPaths, Sets.newHashSet(filters));
    }

    public static PathNodeFilterSet merge(final PathNodeFilterSet a, final PathNodeFilterSet b) {
        if (a.containsExistentials() || b.containsExistentials()) return newExistentialPathNodeFilterSet(
                Sets.union(a.getPositiveExistentialPaths(), b.getPositiveExistentialPaths()),
                Sets.union(a.getNegativeExistentialPaths(), b.getNegativeExistentialPaths()),
                Sets.union(a.getFilters(), b.getFilters()));
        return newRegularPathNodeFilterSet(Sets.union(a.getFilters(), b.getFilters()));
    }

    private static class RegularPathNodeFilterSet extends PathNodeFilterSet {
        protected RegularPathNodeFilterSet(final Set<PathFilter> filters) {
            super(filters);
        }

        @Override
        public Set<Path> getNegativeExistentialPaths() {
            return Collections.emptySet();
        }

        @Override
        public Set<Path> getPositiveExistentialPaths() {
            return Collections.emptySet();
        }

        @Override
        public boolean containsExistentials() {
            return false;
        }

        @Override
        protected PathNodeFilterSet duplicate(final Set<PathFilter> normalisedFilters) {
            return new RegularPathNodeFilterSet(normalisedFilters);
        }
    }

    private static class ExistentialPathNodeFilterSet extends PathNodeFilterSet {
        @Getter(onMethod = @__(@Override))
        private final Set<Path> positiveExistentialPaths, negativeExistentialPaths;

        protected ExistentialPathNodeFilterSet(final Set<PathFilter> filters, final Set<Path> positiveExistentialPaths,
                final Set<Path> negativeExistentialPaths) {
            super(filters);
            assert !positiveExistentialPaths.isEmpty() || !negativeExistentialPaths.isEmpty();
            this.positiveExistentialPaths = positiveExistentialPaths;
            this.negativeExistentialPaths = negativeExistentialPaths;
        }

        @Override
        public boolean containsExistentials() {
            return true;
        }

        @Override
        protected PathNodeFilterSet duplicate(final Set<PathFilter> normalisedFilters) {
            return new ExistentialPathNodeFilterSet(normalisedFilters, positiveExistentialPaths,
                    negativeExistentialPaths);
        }
    }

    /**
     * Constructs the filter using the given filter elements.
     *
     * @param filters
     *         filter elements to be used in the filter
     */
    protected PathNodeFilterSet(final Set<PathFilter> filters) {
        super(filters);
    }

    public PathNodeFilterSet normalise() {
        return duplicate(filters.stream().map(filter -> {
            final PredicateWithArguments<PathLeaf> functionToNormalise = filter.function;
            // step one: transform to uniform function symbols
            final PredicateWithArguments<PathLeaf> uniformFunction =
                    UniformFunctionTranslator.translate(functionToNormalise);
            // step two: sort arguments
            final PredicateWithArguments<PathLeaf> normalFunction = FunctionNormaliser.normalise(uniformFunction);
            return new PathFilter(normalFunction);
        }).sorted().collect(toCollection(LinkedHashSet::new)));
    }

    private int generateHashCode() {
        return Arrays.hashCode(
                getNormalizedPathFilter().getFilters().stream().mapToInt(f -> f.getFunction().hash()).toArray());
    }

    public static boolean equals(final PathNodeFilterSet filter1, final PathNodeFilterSet filter2) {
        return equals(filter1, filter2, new HashMap<>());
    }

    public static boolean equals(final PathNodeFilterSet filter1, final PathNodeFilterSet filter2,
            final Map<Path, Path> pathMap) {
        // TBD and other locations to handle all possible path mappings correctly
        if (filter1.getHashCode() != filter2.getHashCode()) return false;
        final FilterFunctionCompare.PathFilterCompare compare =
                new FilterFunctionCompare.PathFilterCompare(filter1, filter2, pathMap);
        if (!compare.isEqual()) return false;
        if (!filter1.getNegativeExistentialPaths().stream().map(p -> pathMap.get(p)).collect(toSet())
                .equals(filter2.getNegativeExistentialPaths())) {
            return false;
        }
        if (!filter1.getPositiveExistentialPaths().stream().map(p -> pathMap.get(p)).collect(toSet())
                .equals(filter2.getPositiveExistentialPaths())) {
            return false;
        }
        return true;
    }

    @Override
    public <V extends PathFilterListVisitor> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    @Override
    public Iterator<PathNodeFilterSet> iterator() {
        return IteratorUtils.singletonIterator(this);
    }
}
