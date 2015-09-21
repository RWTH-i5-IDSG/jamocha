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
package org.jamocha.function.impls.sideeffects;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.compiler.RuleCompiler;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public abstract class SetRuleCompiler implements Function<Object> {
	public static final String inClips = "set-compiler";
	private static final SlotType[] paramTypes = new SlotType[] { SlotType.SYMBOL };

	@Override
	public SlotType getReturnType() {
		return SlotType.SYMBOL;
	}

	@Override
	public <V extends FunctionVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public String inClips() {
		return inClips;
	}

	@Override
	public SlotType[] getParamTypes() {
		return paramTypes;
	}

	static {
		FunctionDictionary.addFixedArgsGeneratorWithSideEffects(inClips, paramTypes, (
				final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
			return new SetRuleCompiler() {
				@Override
				public Object evaluate(final Function<?>... params) {
					final String name = ((Symbol) params[0].evaluate()).getImage();
					network.setRuleCompiler(RuleCompiler.valueOf(name.toUpperCase()));
					return null;
				}
			};
		});
	}
}