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
package org.jamocha.function.impls;

import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;

/**
 * Loads implementations of the {@link Function} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * @see Function
 * @see FunctionDictionary
 */
public class Functions {

	static {
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.DividedBy.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.Minus.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.Plus.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.Times.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.TimesInverse.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.TypeConverter.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.functions.UnaryMinus.class);
	}
}