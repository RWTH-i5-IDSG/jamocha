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

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.ArrayUtils;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.function.CommutativeFunction;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.fwa.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

/**
 * Compares the Filters.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class FilterFunctionCompare<L extends ExchangeableLeaf<L>> {

    abstract FunctionTypeIdentificationVisitor<L> newFunctionTypeIdentificationVisitor(
            final FilterFunctionCompare<L> context, final FunctionWithArguments<L> fwa);

    private static final class AddressFilterFunctionCompare extends FilterFunctionCompare<ParameterLeaf> {
        final AddressContainer targetAddressContainer;
        final AddressContainer compareAddressContainer;

        private AddressFilterFunctionCompare(final AddressFilter targetFilter, final AddressFilter compareFilter) {
            super();
            this.targetAddressContainer = new AddressContainer(targetFilter);
            this.compareAddressContainer = new AddressContainer(compareFilter);
            targetFilter.getFunction().accept(newFunctionTypeIdentificationVisitor(this, compareFilter.getFunction()));
        }

        @Override
        FunctionTypeIdentificationVisitor<ParameterLeaf> newFunctionTypeIdentificationVisitor(
                final FilterFunctionCompare<ParameterLeaf> context, final FunctionWithArguments<ParameterLeaf> fwa) {
            return new AddressFunctionTypeIdentificationVisitor(context, fwa);
        }

        private final class AddressFunctionTypeIdentificationVisitor
                extends FunctionTypeIdentificationVisitor<ParameterLeaf> {

            private AddressFunctionTypeIdentificationVisitor(final FilterFunctionCompare<ParameterLeaf> context,
                    final FunctionWithArguments<ParameterLeaf> fwa) {
                super(context, fwa);
            }

            @Override
            public void visit(final ParameterLeaf parameterLeaf) {
                this.fwa.accept(new ParameterLeafVisitor(this.context, parameterLeaf));
            }
        }

        private final class ParameterLeafVisitor extends InvalidatingFWAVisitor<ParameterLeaf> {
            final ParameterLeaf compareParameterLeaf;

            private ParameterLeafVisitor(final FilterFunctionCompare<ParameterLeaf> context,
                    final ParameterLeaf parameterLeaf) {
                super(context);
                this.compareParameterLeaf = parameterLeaf;
            }

            @Override
            public void visit(final ParameterLeaf targetParameterLeaf) {
                if (this.compareParameterLeaf.getType() != targetParameterLeaf.getType()) {
                    invalidate();
                }
                try {
                    final SlotInFactAddress compareAddress =
                            AddressFilterFunctionCompare.this.compareAddressContainer.getNextAddress();
                    final SlotInFactAddress targetAddress =
                            AddressFilterFunctionCompare.this.targetAddressContainer.getNextAddress();
                    if (!Objects.equal(compareAddress, targetAddress)) {
                        invalidate();
                    }
                } catch (final IndexOutOfBoundsException e) {
                    invalidate();
                }
            }
        }
    }

    public static final class PathFilterCompare {

        @Getter
        private final Map<Path, Path> pathMap;

        @Getter
        boolean equal = true;

        private boolean comparePaths(final Path comparePath, final Path targetPath) {
            final Path mappedPath = this.pathMap.get(comparePath);
            if (null != mappedPath) {
                return mappedPath == targetPath;
            }
            if (comparePath.template != targetPath.template) {
                return false;
            }
            this.pathMap.put(comparePath, targetPath);
            return true;
        }

        public PathFilterCompare(final PathNodeFilterSet targetNodeFilterSet,
                final PathNodeFilterSet compareNodeFilterSet) {
            this(targetNodeFilterSet, compareNodeFilterSet, new HashMap<>());
        }

        public PathFilterCompare(final PathNodeFilterSet targetNodeFilterSet,
                final PathNodeFilterSet compareNodeFilterSet, final Map<Path, Path> pathMap) {
            super();
            this.pathMap = pathMap;
            final Set<PathFilter> targetFilters = targetNodeFilterSet.normalise().getFilters();
            final Set<PathFilter> compareFilters = compareNodeFilterSet.normalise().getFilters();
            if (targetFilters.size() != compareFilters.size()) {
                this.equal = false;
                return;
            }
            final Set<Path> comparePEP = compareNodeFilterSet.getPositiveExistentialPaths();
            final Set<Path> targetPEP = targetNodeFilterSet.getPositiveExistentialPaths();
            final Set<Path> compareNEP = compareNodeFilterSet.getNegativeExistentialPaths();
            final Set<Path> targetNEP = targetNodeFilterSet.getNegativeExistentialPaths();
            if (comparePEP.size() != targetPEP.size() || compareNEP.size() != targetNEP.size()) {
                this.equal = false;
                return;
            }
            if (!comparePEP.stream().map(pathMap::get).collect(toSet()).containsAll(targetPEP) || !compareNEP.stream()
                    .map(pathMap::get).collect(toSet()).containsAll(targetNEP)) {
                this.equal = false;
                return;
            }
            // TODO handle hash collisions
            for (final Iterator<PathFilter> targetIterator = targetFilters.iterator(), compareIterator =
                    compareFilters.iterator(); targetIterator.hasNext() && compareIterator.hasNext(); ) {
                final PathFilter target = targetIterator.next();
                final PathFilter compare = compareIterator.next();
                if (!new PathFilterFunctionCompare(target, compare).equal) {
                    this.equal = false;
                    return;
                }
            }
        }

        private final class PathFilterFunctionCompare extends FilterFunctionCompare<PathLeaf> {

            private PathFilterFunctionCompare(final PathFilter targetFilterElement,
                    final PathFilter compareFilterElement) {
                super();
                targetFilterElement.getFunction()
                        .accept(newFunctionTypeIdentificationVisitor(this, compareFilterElement.getFunction()));
            }

            @Override
            FunctionTypeIdentificationVisitor<PathLeaf> newFunctionTypeIdentificationVisitor(
                    final FilterFunctionCompare<PathLeaf> context, final FunctionWithArguments<PathLeaf> fwa) {
                return new PathFunctionTypeIdentificationVisitor(context, fwa);
            }

            private final class PathFunctionTypeIdentificationVisitor
                    extends FunctionTypeIdentificationVisitor<PathLeaf> {
                private PathFunctionTypeIdentificationVisitor(final FilterFunctionCompare<PathLeaf> context,
                        final FunctionWithArguments<PathLeaf> fwa) {
                    super(context, fwa);
                }

                @Override
                public void visit(final PathLeaf pathLeaf) {
                    this.fwa.accept(new PathLeafVisitor(this.context, pathLeaf));
                }
            }

            private final class PathLeafVisitor extends InvalidatingFWAVisitor<PathLeaf> {
                final PathLeaf comparePathLeaf;

                private PathLeafVisitor(final FilterFunctionCompare<PathLeaf> context, final PathLeaf pathLeaf) {
                    super(context);
                    this.comparePathLeaf = pathLeaf;
                }

                @Override
                public void visit(final PathLeaf targetPathLeaf) {
                    if (this.comparePathLeaf.getSlot() != targetPathLeaf.getSlot()) {
                        invalidate();
                        return;
                    }
                    if (!comparePaths(this.comparePathLeaf.getPath(), targetPathLeaf.getPath())) invalidate();
                }
            }

        }

    }

    @RequiredArgsConstructor
    static class AddressContainer {
        final AddressFilter addressFilterElement;
        int indexInAddresses = 0;

        final SlotInFactAddress getNextAddress() {
            return this.addressFilterElement.addressesInTarget[this.indexInAddresses++];
        }
    }

    boolean equal = true;

    void invalidate() {
        this.equal = false;
    }

    private boolean isValid() {
        return this.equal;
    }

    @RequiredArgsConstructor
    private abstract static class InvalidatingFWAVisitor<A extends ExchangeableLeaf<A>>
            implements DefaultFunctionWithArgumentsVisitor<A> {
        final FilterFunctionCompare<A> context;

        @Override
        public void defaultAction(final FunctionWithArguments<A> function) {
            this.context.invalidate();
        }
    }

    private abstract static class FunctionTypeIdentificationVisitor<A extends ExchangeableLeaf<A>>
            extends InvalidatingFWAVisitor<A> {
        final FunctionWithArguments<A> fwa;

        protected FunctionTypeIdentificationVisitor(final FilterFunctionCompare<A> context,
                final FunctionWithArguments<A> fwa) {
            super(context);
            this.fwa = fwa;
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<A> predicateWithArgumentsComposite) {
            this.fwa.accept(new CompositeVisitor<A>(this.context, predicateWithArgumentsComposite));
        }

        @Override
        public void visit(final FunctionWithArgumentsComposite<A> functionWithArgumentsComposite) {
            this.fwa.accept(new CompositeVisitor<A>(this.context, functionWithArgumentsComposite));
        }

        @Override
        public void visit(final ConstantLeaf<A> constantLeaf) {
            this.fwa.accept(new ConstantLeafVisitor<A>(this.context, constantLeaf));
        }
    }

    private static class ConstantLeafVisitor<A extends ExchangeableLeaf<A>> extends InvalidatingFWAVisitor<A> {
        final ConstantLeaf<A> constantLeaf;

        protected ConstantLeafVisitor(final FilterFunctionCompare<A> context, final ConstantLeaf<A> constantLeaf) {
            super(context);
            this.constantLeaf = constantLeaf;
        }

        @Override
        public void visit(final ConstantLeaf<A> constantLeaf) {
            if (!Objects.equal(constantLeaf.getValue(), this.constantLeaf.getValue())) {
                this.context.invalidate();
            }
        }
    }

    private static class CompositeVisitor<A extends ExchangeableLeaf<A>> extends InvalidatingFWAVisitor<A> {
        final GenericWithArgumentsComposite<?, ?, A> composite;

        protected CompositeVisitor(final FilterFunctionCompare<A> context,
                final GenericWithArgumentsComposite<?, ?, A> composite) {
            super(context);
            this.composite = composite;
        }

        @AllArgsConstructor
        class Bool {
            boolean equal;
        }

        private void generic(final GenericWithArgumentsComposite<?, ?, A> genericWithArgumentsComposite) {
            if (!Objects.equal(genericWithArgumentsComposite.getFunction().inClips(),
                    this.composite.getFunction().inClips())) {
                this.context.invalidate();
                return;
            }
            final FunctionWithArguments<A>[] addressArgs = genericWithArgumentsComposite.getArgs();
            final FunctionWithArguments<A>[] pathArgs = this.composite.getArgs();
            if (addressArgs.length != pathArgs.length) {
                this.context.invalidate();
                return;
            }
            // compare args normally
            compareArguments(addressArgs, pathArgs);
            // just matches
            if (this.context.isValid()) return;
            // doesn't match, only has a chance if function is commutative
            if (!(genericWithArgumentsComposite.getFunction() instanceof CommutativeFunction<?>)) {
                return;
            }
            // try permutations
            final Map<Integer, List<FunctionWithArguments<A>>> duplicates =
                    Arrays.stream(pathArgs).collect(Collectors.groupingBy(FunctionWithArguments::hash));
            if (!duplicates.values().stream().anyMatch(v -> v.size() > 1)) {
                return;
            }
            final int lcm = duplicates.values().stream().mapToInt(List::size).reduce(1, this::lcm);
            final HashMap<FunctionWithArguments<A>, Integer> indices = IntStream.range(0, pathArgs.length)
                    .collect(HashMap::new,
                            (final HashMap<FunctionWithArguments<A>, Integer> m, final int i) -> m.put(pathArgs[i], i),
                            HashMap<FunctionWithArguments<A>, Integer>::putAll);
            final Bool bool = new Bool(false);
            for (int i = 0; i < lcm; ++i) {
                final int permutation = i;
                duplicates.values().stream().filter(v -> v.size() > 1)
                        .forEach((final List<FunctionWithArguments<A>> v) -> {
                            final int size = v.size();
                            for (int j = 0; j < size; ++j) {
                                pathArgs[indices.get(v.get(j))] =
                                        pathArgs[indices.get(v.get((j + permutation) % size))];
                                if (!bool.equal) {
                                    // equality not yet found to be true
                                    compareArguments(addressArgs, pathArgs);
                                }
                                // else just permute back to original order
                                if (this.context.isValid()) {
                                    // is actually equal
                                    bool.equal = true;
                                } else {
                                    // lets try again
                                    this.context.equal = true;
                                }
                            }
                        });
            }
            if (!bool.equal) {
                this.context.invalidate();
            }
        }

        private void compareArguments(final FunctionWithArguments<A>[] addressArgs,
                final FunctionWithArguments<A>[] pathArgs) {
            for (int i = 0; i < addressArgs.length; i++) {
                final FunctionWithArguments<A> addressFWA = addressArgs[i];
                final FunctionWithArguments<A> pathFWA = pathArgs[i];
                pathFWA.accept(this.context.newFunctionTypeIdentificationVisitor(this.context, addressFWA));
                if (!this.context.isValid()) return;
            }
        }

        /**
         * greatest common divisor
         */
        private int gcd(final int x, final int y) {
            int a = x, b = y;
            while (b > 0) {
                final int temp = b;
                b = a % b; // % is remainder
                a = temp;
            }
            return a;
        }

        /**
         * least common multiple
         */
        private int lcm(final int a, final int b) {
            return a * (b / gcd(a, b));
        }

        @Override
        public void visit(final FunctionWithArgumentsComposite<A> functionWithArgumentsComposite) {
            generic(functionWithArgumentsComposite);
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<A> predicateWithArgumentsComposite) {
            generic(predicateWithArgumentsComposite);
        }
    }

    public static boolean equals(final AddressFilter targetFilter, final AddressFilter compareFilter) {
        return new AddressFilterFunctionCompare(targetFilter, compareFilter).equal;
    }

    static void swap(final int[] list, final int i, final int j) {
        final int tmp = list[i];
        list[i] = list[j];
        list[j] = tmp;
    }

    /**
     * Permutes a list in place
     *
     * @param <T>
     *         element type
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    static class Permutation<T> {
        final List<T> list;
        final int[] p;
        final int length;

        Permutation(final List<T> list) {
            this.length = list.size();
            this.list = list;
            this.p = new int[this.length];
            Arrays.parallelSetAll(this.p, i -> i);
        }

        /**
         * Steps through all permutations of a given list.
         *
         * @return false if the initial permutation is reached again
         */
        public boolean nextPermutation() {
            if (this.length < 2) return false;
            int i = this.length - 1;
            while (true) {
                final int ii = i--;
                // find neighbors with p[n] < p[n+1]
                if (this.p[i] < this.p[ii]) {
                    int j = this.length - 1;
                    // find the last list[m] < p[n]
                    while (this.p[i] >= this.p[j]) {
                        --j;
                    }
                    // Swap p[n] and p[m], and reverse from m+1 to the end
                    swap(this.p, i, j);
                    ArrayUtils.reverse(this.p, ii, this.length);
                    Collections.swap(this.list, i, j);
                    Collections.reverse(this.list.subList(ii, this.length));
                    return true;
                }
                // Neighbors in descending order
                // Is that true for the whole sequence?
                if (0 == i) {
                    // Reverse the sequence to its original order
                    ArrayUtils.reverse(this.p);
                    Collections.reverse(this.list);
                    return false;
                }
            }
        }
    }

    public static void main(final String[] args) {
        final List<Integer> toPermute = new ArrayList<>(Arrays.asList(2, 3, 4, 5, 6, 7, 8));
        final ComponentwisePermutation<Integer> componentwisePermutation =
                new ComponentwisePermutation<>(Arrays.asList(toPermute.subList(0, 2), toPermute.subList(3, 7)));
        do {
            System.out.println(ArrayUtils.toString(toPermute.toArray()));
        } while (componentwisePermutation.nextPermutation());
    }

    /**
     * Permutes a List of Permutations at once
     *
     * @param <T>
     *         element type
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    static class ComponentwisePermutation<T> {
        final List<Permutation<T>> permutators;

        ComponentwisePermutation(final List<List<T>> sublists) {
            this.permutators = new ArrayList<>(sublists.size());
            for (final List<T> list : sublists) {
                this.permutators.add(new Permutation<T>(list));
            }
        }

        /**
         * Steps through all Permutations
         *
         * @return false if the initial permutation is reached again
         */
        public boolean nextPermutation() {
            if (this.permutators.isEmpty()) {
                return false;
            }
            for (final Iterator<Permutation<T>> iter = this.permutators.iterator(); iter.hasNext(); ) {
                final Permutation<T> element = iter.next();
                if (element.nextPermutation()) return true;
                if (!iter.hasNext()) return false;
            }
            return true;
        }
    }

    @Value
    static class Range {
        final int start, end;
    }

    /**
     * Checks if a {@link PathNodeFilterSet} is compatible with an existing {@link Node}.
     *
     * @param targetNode
     *         Node candidate
     * @param pathFilter
     *         PathFilter to compare
     * @return null if not compatible otherwise Map from Paths in pathFilter to FactAddresses in targetNode
     */
    public static Map<Path, FactAddress> equals(final Node targetNode, final PathNodeFilterSet pathFilter) {
        final Set<Path> collectedPaths = PathCollector.newHashSet().collectAllInLists(pathFilter).getPaths().stream()
                .flatMap(p -> p.getJoinedWith().stream()).distinct().collect(toSet());
        if (targetNode.getMemory().getTemplate().length != collectedPaths.size()) {
            return null;
        }

        if (targetNode.getFilter().getNegativeExistentialAddresses().size() != pathFilter.getNegativeExistentialPaths()
                .size() || targetNode.getFilter().getPositiveExistentialAddresses().size() != pathFilter
                .getPositiveExistentialPaths().size()) {
            return null;
        }

        if (targetNode.getFilter().getFilters().size() == 1 && pathFilter.getFilters().size() == 1) {
            final PredicateWithArguments<ParameterLeaf> targetPredicate = FunctionNormaliser.normalise(
                    UniformFunctionTranslator
                            .translate(targetNode.getFilter().getFilters().iterator().next().getFunction()));
            final PredicateWithArguments<PathLeaf> comparePredicate = FunctionNormaliser.normalise(
                    UniformFunctionTranslator.translate(pathFilter.getFilters().iterator().next().getFunction()));
            if (!Arrays.equals(targetPredicate.getParamTypes(), comparePredicate.getParamTypes())) {
                return null;
            }
        }

        final List<Path> pathsPermutation = new LinkedList<>();
        final ComponentwisePermutation<Path> componentwisePermutation;
        // create list of Paths with permutable parts where self-joins occur
        {
            // get representatives for the joined-with-sets (i.e. the edges) and group by nodes
            final Map<Node, Set<Path>> pathSetByNode =
                    PathCollector.newHashSet().collectAllInLists(pathFilter).getPaths().stream()
                            .map(p -> p.getJoinedWith().iterator().next()).distinct()
                            .collect(groupingBy(Path::getCurrentlyLowestNode, toSet()));
            // now we have a set of components consisting of sets of representatives
            final Collection<Set<Path>> components = pathSetByNode.values();
            final List<Range> ranges = new ArrayList<>(components.size());
            // create the permutation ranges
            for (final Set<Path> component : components) {
                assert !component.isEmpty();
                final int start = pathsPermutation.size();
                for (final Path representative : component) {
                    // get one representative path per joined-with-set (i.e. per edge)
                    pathsPermutation.add(representative);
                }
                final int end = pathsPermutation.size();
                if (end - start > 1) {
                    ranges.add(new Range(start, end));
                }
            }
            final List<List<Path>> sublists = new ArrayList<>(ranges.size());
            for (final Range range : ranges) {
                sublists.add(pathsPermutation.subList(range.start, range.end));
            }
            componentwisePermutation = new ComponentwisePermutation<>(sublists);
        }
        //
        do {
            // get current Path permutation
            final List<Path> paths = new ArrayList<>(pathsPermutation);
            final Map<Path, FactAddress> result = new HashMap<>();
            final Edge[] edges = targetNode.getIncomingEdges();
            final Set<Path> joinedPaths = new HashSet<>();
            for (final Edge edge : edges) {
                // search for first path in permutation matching current edge
                final Optional<Path> optMatchingPath =
                        paths.stream().filter(p -> p.getCurrentlyLowestNode() == edge.getSourceNode()).findFirst();
                if (!optMatchingPath.isPresent()) {
                    throw new Error("For one edge no paths were found.");
                }
                final Path matchingPath = optMatchingPath.get();
                // map all paths joined with the found one by the current edge
                for (final Path path : matchingPath.getJoinedWith()) {
                    final FactAddress localizedAddress =
                            edge.localizeAddress(path.getFactAddressInCurrentlyLowestNode());
                    path.cachedOverride(targetNode, localizedAddress, joinedPaths);
                    result.put(path, localizedAddress);
                    joinedPaths.add(path);
                    paths.remove(path);
                }
            }
            assert paths.isEmpty();
            final AddressNodeFilterSet translatedFilter =
                    PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pathFilter, a -> null);
            final AddressNodeFilterSet targetFilter = targetNode.getFilter();
            final boolean equal = equals(translatedFilter, targetFilter);
            for (final Path path : joinedPaths) {
                path.restoreCache();
            }
            if (equal) {
                return result;
            }
        } while (componentwisePermutation.nextPermutation());
        return null;
    }

    public static boolean equals(final AddressNodeFilterSet targetNodeFilterSet,
            final AddressNodeFilterSet translatedNodeFilterSet) {
        final Set<AddressFilter> targetFilters = targetNodeFilterSet.getNormalisedVersion().getFilters();
        final Set<AddressFilter> translatedFilters = translatedNodeFilterSet.getNormalisedVersion().getFilters();
        if (targetFilters.size() != translatedFilters.size()) return false;
        // TODO handle hash collisions
        for (final Iterator<AddressFilter> targetIterator = targetFilters.iterator(), compareIterator =
                translatedFilters.iterator(); targetIterator.hasNext() && compareIterator.hasNext(); ) {
            if (!equals(targetIterator.next(), compareIterator.next())) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final PathNodeFilterSet targetFilter, final PathNodeFilterSet compareFilter) {
        return new PathFilterCompare(targetFilter, compareFilter).equal;
    }
}
