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
package org.jamocha.dn.memory.javaimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jamocha.filter.AddressNodeFilterSet.AddressMatchingConfiguration;
import org.jamocha.filter.MatchingConfigurationElement;

import com.google.common.base.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MatchingProcessor {
    public static List<Row> processMatching(final Row row, final AddressMatchingConfiguration matchingConfiguration) {
        final int index = ((FactAddress) matchingConfiguration.getFactAddress()).index;
        final Fact fact = row.getFactTuple()[index];
        final List<MatchingConfigurationElement> matchingElements = matchingConfiguration.getMatchingElements();
        final Object[] values = (Object[]) fact.getValue(matchingElements.get(0).getAddress());
        if (null == values) {
            return Collections.emptyList();
        }
        final List<Row> result = new ArrayList<>();
        match(values, 0, (final int[] sep) -> {
            final Row copy = row.copy();
            final Fact[] factTuple = copy.getFactTuple();
            factTuple[index] = new Fact.MultislotPatternMatching(factTuple[index], sep);
            result.add(copy);
        }, new int[matchingElements.size()], matchingElements, 0);
        return result;
    }

    public static void match(final Object[] values, final int valuePosition, final Consumer<? super int[]> consumer,
            final int[] separators, final List<MatchingConfigurationElement> matchingElements,
            final int matchingPosition) {
        if (valuePosition == values.length) {
            if (matchingPosition != matchingElements.size()) {
                // can still be valid if the current pattern entity and the rest of the pattern
                // entities are multi patterns (by just finishing this one and matching the rest to
                // be empty)
                for (int pos = matchingPosition; pos < matchingElements.size(); ++pos) {
                    final MatchingConfigurationElement matchingElement = matchingElements.get(pos);
                    if (matchingElement.isSingle()) {
                        return;
                    }
                    separators[pos] = valuePosition;
                }
            }
            consumer.accept(Arrays.copyOf(separators, separators.length - 1));
        } else {
            if (matchingPosition == matchingElements.size()) {
                return;
            }
            final MatchingConfigurationElement matchingElement = matchingElements.get(matchingPosition);
            if (matchingElement.isSingle()) {
                final Optional<?> constant = matchingElement.getConstant();
                if (constant.isPresent() && !Objects.equal(values[valuePosition], constant.get())) {
                    return;
                }
                separators[matchingPosition] = valuePosition + 1;
                match(values, valuePosition + 1, consumer, separators, matchingElements, matchingPosition + 1);
            } else {
                // keep matching items into this multi slot
                match(values, valuePosition + 1, consumer, separators, matchingElements, matchingPosition);
                // additionally: finish this multi slot and go on
                separators[matchingPosition] = valuePosition;
                match(values, valuePosition, consumer, separators, matchingElements, matchingPosition + 1);
            }
        }
    }
}
