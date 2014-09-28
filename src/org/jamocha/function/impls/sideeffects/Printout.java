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
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.logging.LogFormatter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Printout implements Function<Object> {
	public static final String inClips = "printout";

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
		FunctionDictionary.addVarArgsGeneratorWithSideEffects(inClips, (
				final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
			if (paramTypes.length < 1 || paramTypes[0] != SlotType.SYMBOL) {
				return null;
			}
			return new Printout() {
				@Override
				public SlotType[] getParamTypes() {
					return paramTypes;
				}

				@Override
				public Object evaluate(final Function<?>... params) {
					final Symbol logicalName = (Symbol) params[0].evaluate();
					// TBD logical names
					// as long as no further logical names are implemented, we only support printing
					// to the standard output
					if ("nil".equals(logicalName.getImage())) {
						return null;
					}
					assert "t".equals(logicalName.getImage())
							|| "stdout".equals(logicalName.getImage());
					final LogFormatter logFormatter = network.getLogFormatter();
					final StringBuilder sb = new StringBuilder();
					for (int i = 1; i < params.length; ++i) {
						final SlotType type = paramTypes[i];
						final Object value = params[i].evaluate();
						if (type == SlotType.SYMBOL) {
							switch (((Symbol) value).getImage()) {
							case "crlf":
								sb.append(System.getProperty("line.separator"));
								continue;
							case "ff":
								sb.append("\f");
								continue;
							case "tab":
								sb.append("\t");
								continue;
							case "vtab":
								throw new UnsupportedOperationException(
										"Java doesn't support vertical tabs!");
							}
						}
						sb.append(logFormatter.formatSlotValue(type, value));
					}
					System.out.print(sb.toString());
					return null;
				}
			};
		});
	}
}
