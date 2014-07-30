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

import org.apache.logging.log4j.Marker;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.logging.MarkerType;
import org.jamocha.logging.Type;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Watch implements Function<Object> {
	public static String inClips = "watch";

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
		FunctionDictionary.addVarArgsGeneratorWithSideEffects(inClips, SlotType.SYMBOL, (
				final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
			return new Watch() {
				@Override
				public SlotType[] getParamTypes() {
					return paramTypes;
				}

				@Override
				public Object evaluate(final Function<?>... params) {
					final Symbol type = (Symbol) params[0].evaluate();
					switch (type.getImage()) {
					case "all":
						network.getTypedFilter().watchAll();
						break;
					case "facts":
						final Marker[] markers = new Marker[params.length - 1];
						for (int i = 1; i < params.length; ++i) {
							final String string = ((Symbol) params[i].evaluate()).getImage();
							final Template template = network.getTemplate(string);
							if (null == template) {
								network.getLogFormatter().messageArgumentTypeMismatch(network,
										inClips(), i, Type.TEMPLATE);
								return null;
							}
							markers[i - 1] = template.getInstanceMarker();
						}
						network.getTypedFilter().watch(MarkerType.FACTS, markers);
						break;
					case "compilations":
					case "statistics":
					case "focus":
					case "messages":
					case "deffunctions":
					case "globals":
					case "rules":
					case "activations":
					case "instances":
					case "slots":
					case "message-handlers":
					case "generic-functions":
					case "methods":
						throw new UnsupportedOperationException("Unsupported yet: "
								+ type.getImage());
					default:
						network.getLogFormatter().messageArgumentTypeMismatch(network, inClips(),
								1, Type.WATCHABLE_SYMBOL);
					}
					return null;
				}
			};
		});
	}
}
