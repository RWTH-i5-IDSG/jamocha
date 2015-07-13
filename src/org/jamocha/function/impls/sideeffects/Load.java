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
package org.jamocha.function.impls.sideeffects;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Load extends Predicate {
	public static final String inClips = "load";
	private static final SlotType[] symbol = new SlotType[] { SlotType.SYMBOL };
	private static final SlotType[] string = new SlotType[] { SlotType.STRING };

	@Override
	public String inClips() {
		return inClips;
	}

	@Override
	public <V extends FunctionVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	static {
		FunctionDictionary.addFixedArgsGeneratorWithSideEffects(inClips, symbol,
				(final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
					return new Load() {
						@Override
						public Boolean evaluate(final Function<?>... params) {
							return network.loadFromFile(((Symbol) params[0].evaluate()).getImage(), true);
						}

						@Override
						public SlotType[] getParamTypes() {
							return symbol;
						}
					};
				});
	}

	static {
		FunctionDictionary.addFixedArgsGeneratorWithSideEffects(inClips, string,
				(final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
					return new Load() {
						@Override
						public Boolean evaluate(final Function<?>... params) {
							return network.loadFromFile(((String) params[0].evaluate()), true);
						}

						@Override
						public SlotType[] getParamTypes() {
							return string;
						}
					};
				});
	}
}
