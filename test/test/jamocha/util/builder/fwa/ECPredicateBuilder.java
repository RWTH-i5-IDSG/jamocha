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
 * the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.util.builder.fwa;

import org.jamocha.filter.ECFilter;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.languages.common.ScopeStack;

import static org.jamocha.util.ToArray.toArray;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECPredicateBuilder extends ECGenericBuilder<Boolean, Predicate, ECPredicateBuilder> {
	public ECPredicateBuilder(final Predicate function, final ScopeStack.Scope scope) {
		super(function, scope);
	}

	@Override
	public PredicateWithArguments<ECLeaf> build() {
		if (this.function.getParamTypes().length != this.args.size()) {
			throw new IllegalArgumentException("Wrong number of arguments!");
		}
		return new PredicateWithArgumentsComposite<ECLeaf>(this.function,
				toArray(this.args, FunctionWithArguments[]::new));
	}

	public ECFilter buildFilter() {
		return new ECFilter(build());
	}
}