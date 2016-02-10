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

import static java.util.stream.Collectors.toCollection;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.Data;

import lombok.experimental.UtilityClass;
import org.jamocha.dn.memory.CounterColumn;
import org.jamocha.dn.memory.CounterColumnMatcher;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;
import org.jamocha.filter.AddressNodeFilterSet.AddressMatchingConfiguration;
import org.jamocha.filter.AddressNodeFilterSet.ExistentialAddressFilter;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwatransformer.FWAPathToAddressTranslator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
public class PathNodeFilterSetToAddressNodeFilterSetTranslator {
    public static AddressNodeFilterSet translate(final PathNodeFilterSet pathFilter,
            final CounterColumnMatcher filterElementToCounterColumn) {
        return translate(pathFilter, pathFilter.normalise(), filterElementToCounterColumn);
    }

    public static AddressNodeFilterSet translate(final PathNodeFilterSet pathFilter,
            final PathNodeFilterSet normalisedVersion, final CounterColumnMatcher counterColumnMatcher) {
        final HashSet<AddressFilter> filters = translateFilters(pathFilter, counterColumnMatcher, HashSet::new);
        final LinkedHashSet<AddressFilter> normalFilters =
                translateFilters(normalisedVersion, counterColumnMatcher, LinkedHashSet::new);
        return new AddressNodeFilterSet(toFactAddressSet(pathFilter.getPositiveExistentialPaths()),
                toFactAddressSet(pathFilter.getNegativeExistentialPaths()), filters, normalFilters,
                new ArrayList<AddressMatchingConfiguration>());
    }

    private static <R extends Set<AddressFilter>> R translateFilters(final PathNodeFilterSet pathFilter,
            final CounterColumnMatcher filterElementToCounterColumn, final Supplier<R> collectionFactory) {
        return pathFilter.getFilters().stream()
                .map(f -> f.accept(new PathFilterTranslator(filterElementToCounterColumn)).result)
                .collect(toCollection(collectionFactory));
    }

    static Set<FactAddress> toFactAddressSet(final Set<Path> existentialPaths) {
        return existentialPaths.stream().map(Path::getFactAddressInCurrentlyLowestNode).collect(Collectors.toSet());
    }

    @Data
    static class PathFilterTranslator implements PathFilterSetVisitor {
        final CounterColumnMatcher counterColumnMatcher;
        AddressFilter result;

        @Override
        public void visit(final PathFilter pathFilter) {
            final ArrayList<SlotInFactAddress> addresses = new ArrayList<>();
            final PredicateWithArguments<ParameterLeaf> predicateWithArguments =
                    FWAPathToAddressTranslator.translate(pathFilter.getFunction(), addresses);
            final SlotInFactAddress[] addressArray = toArray(addresses, SlotInFactAddress[]::new);
            final CounterColumn counterColumn = counterColumnMatcher.getCounterColumn(pathFilter);
            if (null == counterColumn) {
                this.result = new AddressFilter(predicateWithArguments, addressArray);
            } else {
                this.result = new ExistentialAddressFilter(predicateWithArguments, addressArray, counterColumn);
            }
        }

        @Override
        public void visit(final PathExistentialSet existential) {
            existential.getExistentialClosure().accept(this);
        }
    }
}
