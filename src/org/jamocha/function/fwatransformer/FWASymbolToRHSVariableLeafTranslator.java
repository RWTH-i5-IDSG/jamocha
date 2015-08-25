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
package org.jamocha.function.fwatransformer;

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.RHSVariableLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwa.VariableValueContext;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class FWASymbolToRHSVariableLeafTranslator extends FWATranslator<SymbolLeaf, RHSVariableLeaf> {
	final Map<EquivalenceClass, PathLeaf> ec2PathLeaf;
	final VariableValueContext context;

	@Override
	public FWATranslator<SymbolLeaf, RHSVariableLeaf> of() {
		return new FWASymbolToRHSVariableLeafTranslator(ec2PathLeaf, context);
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static FunctionWithArguments<RHSVariableLeaf>[] translate(final Map<EquivalenceClass, PathLeaf> ec2PathLeaf,
			final VariableValueContext context, final FunctionWithArguments<SymbolLeaf>... actions) {
		final FWASymbolToRHSVariableLeafTranslator instance =
				new FWASymbolToRHSVariableLeafTranslator(ec2PathLeaf, context);
		return toArray(Arrays.stream(actions).map(fwa -> fwa.accept(instance).functionWithArguments),
				FunctionWithArguments[]::new);
	}

	@Override
	public void visit(final SymbolLeaf symbolLeaf) {
		final VariableSymbol symbol = symbolLeaf.getSymbol();
		final PathLeaf pathLeaf = ec2PathLeaf.get(symbol.getEqual());
		if (pathLeaf != null) {
			// variable is bound on the LHS, add initializer
			final SlotInFactAddress slotInFactAddress =
					new SlotInFactAddress(pathLeaf.getPath().getFactAddressInCurrentlyLowestNode(), pathLeaf.getSlot());
			context.addInitializer(symbol, slotInFactAddress);
		}
		this.functionWithArguments = new RHSVariableLeaf(context, symbol, symbolLeaf.getReturnType());
	}
}