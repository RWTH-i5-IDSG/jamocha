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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.function.fwa.*;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class FWASymbolToECTranslator extends FWATranslator<SymbolLeaf, ECLeaf> {
	final ScopeStack.Scope scope;

	public static PredicateWithArguments<ECLeaf> translate(final ScopeStack.Scope scope,
			final PredicateWithArguments<SymbolLeaf> pwa) {
		return (PredicateWithArguments<ECLeaf>) translate(scope, (FunctionWithArguments<SymbolLeaf>) pwa);
	}

	public static FunctionWithArguments<ECLeaf> translate(final ScopeStack.Scope scope,
			final FunctionWithArguments<SymbolLeaf> pwa) {
		return pwa.accept(new FWASymbolToECTranslator(scope)).functionWithArguments;
	}

	@Override
	public FWATranslator<SymbolLeaf, ECLeaf> of() {
		return new FWASymbolToECTranslator(this.scope);
	}

	@Override
	public void visit(final SymbolLeaf symbolLeaf) {
		this.functionWithArguments = new ECLeaf(symbolLeaf.getSymbol().getEqual());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
		this.functionWithArguments = new ECLeaf(
				RuleCondition.EquivalenceClass.newECFromConstantExpression(scope, new ConstantLeaf<>(constantLeaf)));
	}
}