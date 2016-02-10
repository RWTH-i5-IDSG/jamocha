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

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * Interface for rating providers.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 */
public interface RatingProvider {
    double rateVirtualAlpha(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Set<PathFilterList> preNetwork);

    double rateMaterialisedAlpha(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Set<PathFilterList> preNetwork);

    double rateBeta(final StatisticsProvider statisticsProvider, final PathNodeFilterSet toRate,
            final Map<Set<PathFilterList>, List<Pair<List<Set<PathFilterList>>, List<PathFilter>>>>
                    componentToJoinOrder,
            final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents);

    double rateNetwork(final SideEffectFunctionToNetwork network);
}
