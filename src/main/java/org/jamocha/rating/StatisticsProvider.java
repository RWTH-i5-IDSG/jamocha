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
package org.jamocha.rating;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Value;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface StatisticsProvider {
    @Value
    class Data {
        /**
         * effective insert frequency
         */
        final double finsert;
        /**
         * effective delete frequency
         */
        final double fdelete;
        /**
         * filtered size
         */
        final double rowCount;
        /**
         * number of facts per tuple
         */
        final double tupleSize;
    }

    Data getData(final Set<PathFilterList> filters);

    void setData(final Set<PathFilterList> filters, final Data data);

    double getSelectivity(final PathNodeFilterSet filters, final Set<PathFilterList> preNetwork);

    /**
     * The inputComponent is expected to be a regular Component.
     *
     * For existential components, the joinOrder contains exactly one component in the left part of the pair and exactly
     * one filter in the right part of the pair.
     *
     * @param inputComponent
     * @param joinOrder
     * @param regularComponents
     * @param pathToPreNetworkComponents
     * @return as many JSFs as there are entries in the joinOrder list
     */
    double[] getRegularJSFs(final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Set<Set<PathFilterList>> regularComponents,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents);

    /**
     * In case the input component is meant to be existential, the joinOrder is expected to not contain the other
     * existential components and their filters.
     *
     * For existential components, the joinOrder contains exactly one component in the left part of the pair and exactly
     * one filter in the right part of the pair.
     *
     * @param inputComponent
     * @param joinOrder
     * @param pathToPreNetworkComponents
     * @return as many JSFs as there are entries in the joinOrder list
     */
    double[] getAllJSFs(final Set<PathFilterList> inputComponent,
            final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents);

    double getJSF(final Set<Set<PathFilterList>> regularComponents, final Set<PathFilterList> existentialComponent,
            final PathFilter existentialFilter, final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents);

    double getPageSize();
}
