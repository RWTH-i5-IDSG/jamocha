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

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.languages.common.SingleFactVariable;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString(of = { "rule", "filters" })
public class ExistentialProxy {
	final Rule rule;
	final ECExistentialSet existential;
	final Set<Filter> filters = new HashSet<>();
	final Set<SingleFactVariable> factvariables;
	final Either<Rule, ExistentialProxy> either;

	public ExistentialProxy(final Rule rule, final ECExistentialSet existential) {
		this.rule = rule;
		this.existential = existential;
		this.factvariables = existential.getExistentialFactVariables();
		this.either = Either.right(this);
	}

	public Filter.FilterInstance getExistentialClosure() {
		return this.rule.getExistentialProxies().inverse().get(this);
	}
}