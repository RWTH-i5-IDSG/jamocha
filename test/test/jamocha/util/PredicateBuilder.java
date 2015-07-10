/*
 * Copyright 2002-2013 The Jamocha Team
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
package test.jamocha.util;

import static org.jamocha.util.ToArray.toArray;

import org.jamocha.filter.PathFilter;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class PredicateBuilder extends GenericBuilder<Boolean, Predicate, PredicateBuilder> {
	public PredicateBuilder(final Predicate function) {
		super(function);
	}

	@Override
	public PredicateWithArguments<PathLeaf> build() {
		if (this.function.getParamTypes().length != this.args.size()) {
			throw new IllegalArgumentException("Wrong number of arguments!");
		}
		return new PredicateWithArgumentsComposite<PathLeaf>(this.function, toArray(this.args,
				FunctionWithArguments[]::new));
	}

	public PathFilter buildFilter() {
		return new PathFilter(build());
	}
}