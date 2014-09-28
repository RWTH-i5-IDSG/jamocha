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
import org.jamocha.function.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Run implements Function<Object> {
	public static final String inClips = "run";

	@Override
	public SlotType getReturnType() {
		return SlotType.NIL;
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

	static {
		FunctionDictionary.addFixedArgsGeneratorWithSideEffects(inClips, SlotType.empty, (
				final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
			return new Run() {
				@Override
				public SlotType[] getParamTypes() {
					return SlotType.empty;
				}

				@Override
				public Object evaluate(final Function<?>... params) {
					// TODO run!
					return null;
				}
			};
		});
		final SlotType[] oneInt = new SlotType[] { SlotType.LONG };
		FunctionDictionary.addFixedArgsGeneratorWithSideEffects(inClips, oneInt, (
				final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
			return new Run() {
				@Override
				public SlotType[] getParamTypes() {
					return oneInt;
				}

				@Override
				public Object evaluate(final Function<?>... params) {
					// TODO run!
					// (Long) params[0].evaluate()
					return null;
				}
			};
		});
	}
}
