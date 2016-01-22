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

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class FWASymbolToECTranslator extends FWATranslator<SymbolLeaf, ECLeaf> {

	public static PredicateWithArguments<ECLeaf> translate(final PredicateWithArguments<SymbolLeaf> pwa) {
		return (PredicateWithArguments<ECLeaf>) translate((FunctionWithArguments<SymbolLeaf>) pwa);
	}

	public static FunctionWithArguments<ECLeaf> translate(final FunctionWithArguments<SymbolLeaf> pwa) {
		return pwa.accept(new FWASymbolToECTranslator()).functionWithArguments;
	}

	@Override
	public FWATranslator<SymbolLeaf, ECLeaf> of() {
		return new FWASymbolToECTranslator();
	}

	@Override
	public void visit(final SymbolLeaf symbolLeaf) {
		this.functionWithArguments = new ECLeaf(symbolLeaf.getSymbol().getEqual());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
		this.functionWithArguments = new ConstantLeaf<>(constantLeaf);
	}
}