/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.function.fwatransformer;

import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;

public class FWADeepCopy<L extends ExchangeableLeaf<L>> extends FWATranslator<L, L> {

	@SuppressWarnings("unchecked")
	public static <L extends ExchangeableLeaf<L>, T extends FunctionWithArguments<L>> T copy(final T fwa) {
		return (T) fwa.accept(new FWADeepCopy<>()).getFunctionWithArguments();
	}

	@Override
	public FWADeepCopy<L> of() {
		return new FWADeepCopy<>();
	}

	@Override
	public void visit(final L fwa) {
		this.functionWithArguments = fwa.copy();
	}
}