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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterListSetFlattener;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
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
		return componentToData.computeIfAbsent(
				componentToFlatComponent.computeIfAbsent(filters, PathFilterListSetFlattener::flatten),
				f -> identifyTemplate(f));
	}

	private Data identifyTemplate(final Set<PathNodeFilterSet> nodeSets) {
		if (nodeSets.size() != 1)
			return dummyData;
		final PathNodeFilterSet nodeSet = nodeSets.iterator().next();
		final Set<PathFilter> filters = nodeSet.getFilters();
		if (filters.size() != 1) {
			return dummyData;
		}
		final PredicateWithArguments<PathLeaf> predicate = filters.iterator().next().getFunction();
		if (!(predicate instanceof PredicateWithArgumentsComposite)) {
			return dummyData;
		}
		final FunctionWithArguments<PathLeaf>[] args =
				((PredicateWithArgumentsComposite<PathLeaf>) predicate).getArgs();
		if (args.length != 1) {
			return dummyData;
		}
		final FunctionWithArguments<PathLeaf> arg = args[0];
		if (!(arg instanceof PathLeaf)) {
			return dummyData;
		}
		return templateToData.getOrDefault(((PathLeaf) arg).getPath().getTemplate().getName(), dummyData);
	}

	// Workaround for evaluation - replace with actual data in OTN memory
	private static final Map<String, Data> templateToData = new HashMap<>();
	private static final Data dummyData = new Data(10, 10, 100, 1);
	{
		templateToData.put("stage", new Data(10, 10, 1, 1));
		templateToData.put("line", new Data(20, 20, 1000, 1));
		templateToData.put("edge", new Data(100, 100, 2000, 1));
		templateToData.put("junction", new Data(30, 30, 800, 1));
	}

	@Override
	public void setData(final Set<PathFilterList> filters, final Data data) {
		componentToData.put(componentToFlatComponent.computeIfAbsent(filters, PathFilterListSetFlattener::flatten),
				data);
	}

	private static DoubleBinaryOperator times = (a, b) -> a * b;

	private static double getSelectivity(final PathFilter filter) {
		final String string = filter.getFunction().toString();
		if (string.startsWith("'and'")) {
			final FunctionWithArguments<PathLeaf>[] args =
					((PredicateWithArgumentsComposite<PathLeaf>) filter.getFunction()).getArgs();
			return Arrays.stream(args).mapToDouble(arg -> getSelectivity(arg.toString())).reduce(1.0, times);
		}
		return getSelectivity(string);
	}

	private static double getSelectivity(final String string) {
		if (string.matches("'='\\(Path\\d+ \\[edge::label\\], nil\\)")) {
			return 0.85;
		} else if (string.matches("'TRUE'\\(.+\\)")) {
			return 1.0;
		} else if (string.matches("'='\\(Path\\d+ \\[edge::joined\\], false\\)")) {
			return 0.75;
		} else if (string.matches("'='\\(Path\\d+ \\[edge::p1\\], Path\\d+ \\[edge::p1\\]\\)")) {
			return 0.05;
		} else if (string.matches("'='\\(Path\\d+ \\[edge::p1\\], Path\\d+ \\[junction::base_point\\]\\)")) {
			return 0.05;
		} else if (string.matches("'='\\(Path\\d+ \\[junction::base_point\\], Path\\d+ \\[edge::p1\\]\\)")) {
			return 0.05;
		} else if (string.matches("'not'\\('='\\(Path\\d+ \\[edge::p1\\], Path\\d+ \\[edge::p1\\]\\)\\)")) {
			return 0.95;
		} else if (string.matches("'not'\\('='\\(Path\\d+ \\[edge::p2\\], Path\\d+ \\[edge::p2\\]\\)\\)")) {
			return 0.95;
		} else if (string.matches("'='\\(Path\\d+ \\[junction::jtype\\], .+\\)")) {
			return 0.3;
		}
		return standardJSF;
	}

	@Override
	public double getSelectivity(final PathNodeFilterSet filters, final Set<PathFilterList> preNetwork) {
		final Set<PathNodeFilterSet> flattenedPreNetwork = PathFilterListSetFlattener.flatten(preNetwork);
		if (flattenedPreNetwork.contains(filters))
			return 1;
		return filters.getFilters().stream().mapToDouble(fi -> getSelectivity(fi)).reduce(1.0, times);

		// return getDummyJSFByTests(filters);
		// TBD implement to get actual data from somewhere (statistic gatherer?)
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
		return new double[] { getDummyJSFByTests(joinOrder) };
		// return new double[] { getDummyJSFByTests(joinOrder) };

		// TBD implement to get actual data from somewhere (statistic gatherer?)
		// final int joinOrderSize =
		// (int)
		// joinOrder.stream().map(Pair::getLeft).filter(Objects::nonNull).flatMap(List::stream).count();
		// final double jsfPerJoin = Math.pow(standardJSF, 1. / joinOrderSize);
		//
		// final double[] result = new double[joinOrderSize];
		// Arrays.fill(result, jsfPerJoin);
		// return result;
	}

	@Override
	public double[] getAllJSFs(final Set<PathFilterList> inputComponent,
			final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder,
			final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
		// TBD implement to get actual data from somewhere (statistic gatherer?)
		return new double[] { getDummyJSFByTests(joinOrder) };

		// final int joinOrderSize = joinOrder.size();
		// final long numRegularComponents =
		// joinOrder.stream().map(Pair::getLeft).filter(Objects::nonNull).flatMap(List::stream).count();
		// final double jsfPerJoin = Math.pow(nodeJSF, 1. / numRegularComponents);
		//
		// final double[] result = new double[joinOrderSize];
		// Arrays.fill(result, jsfPerJoin);
		// return result;
	}

	private double getDummyJSFByTests(final List<Pair<List<Set<PathFilterList>>, List<PathFilter>>> joinOrder) {
		return joinOrder.stream().map(Pair::getRight).flatMap(List::stream)
				.mapToDouble(StatisticsProvider::getSelectivity).reduce(1.0, times);
	}

	@Override
	public double getJSF(final Set<Set<PathFilterList>> regularComponents,
			final Set<PathFilterList> existentialComponent, final PathFilter existentialFilter,
			final Map<Path, Set<PathFilterList>> pathToPreNetworkComponents) {
		// TBD implement to get actual data from somewhere (statistic gatherer?)
		return standardJSF;
	}
}
