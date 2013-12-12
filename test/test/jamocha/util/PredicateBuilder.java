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

import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.PredicateWithArgumentsComposite;

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
	public PredicateWithArguments build() {
		if (this.function.getParamTypes().length != this.args.size()) {
			throw new IllegalArgumentException("Wrong number of arguments!");
		}
		return new PredicateWithArgumentsComposite(this.function,
				this.args.toArray(new FunctionWithArguments[this.args.size()]));
	}
}