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
package org.jamocha.rating.fraj;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterListSetFlattener;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class StatisticsProvider implements org.jamocha.rating.StatisticsProvider {

	private static final double standardJSF = 0.6;
	private static final double pageSize = 1000;

	@Override
	public double getPageSize() {
		return pageSize;
	}

	// TODO limit the caching capacity?
	private final LinkedHashMap<Set<PathNodeFilterSet>, Data> componentToData = new LinkedHashMap<>();
	private final LinkedHashMap<Set<PathFilterList>, Set<PathNodeFilterSet>> componentToFlatComponent =
			new LinkedHashMap<>();

	@Override
	public Data getData(final Set<PathFilterList> filters) {
		return componentToData.get(componentToFlatComponent.computeIfAbsent(filters,
				PathFilterListSetFlattener::flatten));
	}

	@Override
	public void setData(final Set<PathFilterList> filters, final Data data) {
		componentToData.put(componentToFlatComponent.computeIfAbsent(filters, PathFilterListSetFlattener::flatten),
				data);
	}

	@Override
	public double getSelectivity(final PathNodeFilterSet filters, final Set<PathFilterList> preNetwork) {
		final Set<PathNodeFilterSet> flattenedPreNetwork = PathFilterListSetFlattener.flatten(preNetwork);
		if (flattenedPreNetwork.contains(filters))
			return 1;
		// TBD implement to get actual data from somewhere (statistic gatherer?)
		int numTests = filters.getFilters().stream().mapToInt(filter -> ((PredicateWithArgumentsComposite<?>)filter.getFunction()).getFunction().getParamTypes().length).sum();
		return Math.pow(standardJSF, numTests);
	}

	/*
	 * restrictions regarding the JSFs:
	 * 
	 * symmetry JSF_A,B(F) = JSF_B,A(F)
	 * 
	 * \Prod_i jsf[i] has to be fixed independently of the order of the i's
	 */

	@Override
	public double[] getRegularJSFs(final Set<PathFilterList> inputComponent,
			final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
			final Set<Set<PathFilterList>> regularComponents,
			final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
		// TBD implement to get actual data from somewhere (statistic gatherer?)
		final int joinOrderSize = joinOrder.size();
		final double jsfPerJoin = Math.pow(standardJSF, 1. / joinOrderSize);
		
		double[] result = new double[joinOrderSize];
		Arrays.fill(result,jsfPerJoin);
		return result;
	}

	@Override
	public double[] getAllJSFs(final Set<PathFilterList> inputComponent,
			final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
			final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
		// TBD implement to get actual data from somewhere (statistic gatherer?)
		final int joinOrderSize = joinOrder.size();
		final long numRegularComponents = joinOrder.stream().map(pair -> pair.getLeft()).filter(Objects::nonNull).count();
		final double jsfPerJoin = Math.pow(standardJSF, 1. / numRegularComponents);
		
		double[] result = new double[joinOrderSize];
		Arrays.fill(result, jsfPerJoin);
		return result;
	}

	@Override
	public double getJSF(final Set<Set<PathFilterList>> regularComponents,
			final Set<PathFilterList> existentialComponent, final PathFilter existentialFilter,
			final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
		// TBD implement to get actual data from somewhere (statistic gatherer?)
		return standardJSF;
	}
}
