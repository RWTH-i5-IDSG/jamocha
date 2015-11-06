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
package org.jamocha.dn.compiler.ecblocks;

import static java.util.stream.Collectors.toSet;

import java.util.Set;

import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.languages.common.SingleFactVariable;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class Util {
	protected static Set<Filter> getFilters(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFilters, ExistentialProxy::getFilters);
	}

	protected static Set<Filter> getFilters(final RowIdentifier row) {
		return getFilters(row.getRuleOrProxy());
	}

	protected static Set<SingleFactVariable> getFactVariables(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return ruleOrProxy.fold(Rule::getFactvariables, ExistentialProxy::getFactvariables);
	}

	protected static Set<SingleFactVariable> getFactVariables(final RowIdentifier row) {
		return getFactVariables(row.getRuleOrProxy());
	}

	protected static Set<FilterInstance> getFilterInstances(final Either<Rule, ExistentialProxy> ruleOrProxy) {
		return getFilters(ruleOrProxy).stream().flatMap(f -> f.getAllInstances(ruleOrProxy).stream()).collect(toSet());
	}
}
