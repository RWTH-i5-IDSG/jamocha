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

import lombok.Getter;
import lombok.Value;
import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TypeLeaf;
import org.jamocha.languages.common.RuleCondition;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ExistentialInfo {
    boolean isExistential();

    boolean isPositive();

    int[] getExistentialArguments();

    ExistentialInfo REGULAR = new ExistentialInfo() {
        @Override
        public boolean isExistential() {
            return false;
        }

        @Override
        public boolean isPositive() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int[] getExistentialArguments() {
            throw new UnsupportedOperationException();
        }
    };

    @Value
    class PositiveExistentialInfo implements ExistentialInfo {
        @Getter(onMethod = @__({@Override}))
        final int[] existentialArguments;

        @Override
        public boolean isPositive() {
            return true;
        }

        @Override
        public boolean isExistential() {
            return true;
        }
    }

    @Value
    class NegatedExistentialInfo implements ExistentialInfo {
        @Getter(onMethod = @__({@Override}))
        final int[] existentialArguments;

        @Override
        public boolean isPositive() {
            return false;
        }

        @Override
        public boolean isExistential() {
            return true;
        }
    }

    static ExistentialInfo get(final ECFilterSet.ECExistentialSet existentialSet) {
        return get(existentialSet.getExistentialClosure(), existentialSet.getEquivalenceClasses(),
                existentialSet.isPositive());
    }

    static ExistentialInfo get(final ECFilter filter,
            final Set<RuleCondition.EquivalenceClass> existentialEquivalenceClasses, final boolean positive) {
        final ArrayList<RuleCondition.EquivalenceClass> collect = OrderedECCollector.collect(filter.getFunction());
        final int[] existentialParameters =
                IntStream.range(0, collect.size()).filter(i -> existentialEquivalenceClasses.contains(collect.get(i)))
                        .toArray();
        final ExistentialInfo existentialInfo =
                positive ? new ExistentialInfo.PositiveExistentialInfo(existentialParameters)
                        : new ExistentialInfo.NegatedExistentialInfo(existentialParameters);
        return existentialInfo;
    }

    @Value
    class FunctionWithExistentialInfo {
        FunctionWithArguments<TypeLeaf> function;
        ExistentialInfo existentialInfo;
    }
}
