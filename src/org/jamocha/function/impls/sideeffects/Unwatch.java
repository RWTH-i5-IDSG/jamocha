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

import java.util.Arrays;

import org.apache.logging.log4j.Marker;
import org.jamocha.dn.ConstructCache.Defrule;
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
public abstract class Unwatch implements Function<Object> {
	public static final String inClips = "unwatch";
	private static final SlotType[] paramTypes = new SlotType[] { SlotType.SYMBOL };

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

	@Override
	public SlotType[] getParamTypes() {
		return paramTypes;
	}

	private static <T> void unwatch(final SideEffectFunctionToNetwork network, final MarkerType markerType,
			final Type type, final java.util.function.Function<String, T> nameToInstance,
			final java.util.function.Function<T, Marker> instanceToMarker, final Function<?>... params) {
		Watch.parseArguments(network, markerType, type, nameToInstance, instanceToMarker,
				network.getTypedFilter()::unwatch, params);
	}

	static {
		FunctionDictionary.addVarArgsGeneratorWithSideEffects(
				inClips,
				(final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
					if (paramTypes.length < 1
							|| !Arrays.equals(paramTypes, SlotType.nCopies(SlotType.SYMBOL, paramTypes.length))) {
						return null;
					}
					return new Unwatch() {
						@Override
						public Object evaluate(final Function<?>... params) {
							final Symbol type = (Symbol) params[0].evaluate();
							switch (type.getImage()) {
							case "all":
								network.getTypedFilter().unwatchAll();
								break;
							case "facts":
								unwatch(network, MarkerType.FACTS, Type.TEMPLATE, network::getTemplate,
										Template::getInstanceMarker, params);
								break;
							case "rules":
								unwatch(network, MarkerType.RULES, Type.WATCHABLE_SYMBOL, network::getRule,
										Defrule::getFireMarker, params);
								break;
							case "activations":
								unwatch(network, MarkerType.ACTIVATIONS, Type.WATCHABLE_SYMBOL, network::getRule,
										Defrule::getActivationMarker, params);
								break;
							case "compilations":
							case "statistics":
							case "focus":
							case "messages":
							case "deffunctions":
							case "globals":
							case "instances":
							case "slots":
							case "message-handlers":
							case "generic-functions":
							case "methods":
								throw new UnsupportedOperationException("Unsupported yet: " + type.getImage());
							default:
								network.getLogFormatter().messageArgumentTypeMismatch(network, inClips(), 1,
										Type.WATCHABLE_SYMBOL);
							}
							return null;
						}
					};
				});
	}
}
