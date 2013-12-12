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
package org.jamocha.filter.impls;

import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Predicate;

/**
 * Loads implementations of the more specific {@link Predicate} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Predicate
 * @see FunctionDictionary
 */
public class Predicates {

	static {
		FunctionDictionary.addImpl(org.jamocha.filter.impls.predicates.And.class);
		FunctionDictionary.addImpl(org.jamocha.filter.impls.predicates.Equals.class);
		FunctionDictionary.addImpl(org.jamocha.filter.impls.predicates.Greater.class);
		FunctionDictionary.addImpl(org.jamocha.filter.impls.predicates.GreaterOrEqual.class);
		FunctionDictionary.addImpl(org.jamocha.filter.impls.predicates.Less.class);
		FunctionDictionary.addImpl(org.jamocha.filter.impls.predicates.LessOrEqual.class);
	}

}
