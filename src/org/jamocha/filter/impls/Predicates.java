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

import org.jamocha.filter.Predicate;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.impls.predicates.And;
import org.jamocha.filter.impls.predicates.Equals;
import org.jamocha.filter.impls.predicates.Less;

/**
 * Loads implementations of the more specific {@link Predicate} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Predicate
 * @see FunctionDictionary
 */
public class Predicates {

	static {
		FunctionDictionary.addImpl(Less.class);
		FunctionDictionary.addImpl(Equals.class);
		FunctionDictionary.addImpl(And.class);
	}

}
