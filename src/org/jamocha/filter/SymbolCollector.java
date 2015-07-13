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
package org.jamocha.filter;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

import lombok.Getter;

/**
 * Provides ease-of-use-methods for symbols used within the RuleCondition.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <T>
 *            collection type to use while collecting the paths
 */
public class SymbolCollector {
	@Getter
	private Set<VariableSymbol> symbols;

	public SymbolCollector(final RuleCondition rc) {
		this.symbols = rc.getVariableSymbols();
	}

	public Set<VariableSymbol> getNonDummySymbols() {
		return symbols.stream().filter(s -> !s.isDummy()).collect(toSet());
	}

	public Set<VariableSymbol> getDummySymbols() {
		return symbols.stream().filter(Symbol::isDummy).collect(toSet());
	}

	private Stream<Pair<VariableSymbol, SingleSlotVariable>> getSlotVariableStream() {
		return this.symbols.stream().flatMap(symbol -> StreamSupport
				.stream(symbol.getEqual().getEqualSlotVariables().spliterator(), true).map(sv -> Pair.of(symbol, sv)));
	}

	public Map<SingleFactVariable, Pair<VariableSymbol, List<Pair<VariableSymbol, SingleSlotVariable>>>> toSlotVariablesByFactVariable() {
		final Map<SingleFactVariable, List<Pair<VariableSymbol, SingleSlotVariable>>> fvToSv =
				getSlotVariableStream().collect(groupingBy(pvs -> pvs.getRight().getFactVariable()));
		final Map<SingleFactVariable, Pair<VariableSymbol, List<Pair<VariableSymbol, SingleSlotVariable>>>> map =
				new HashMap<>();
		for (final Map.Entry<SingleFactVariable, List<Pair<VariableSymbol, SingleSlotVariable>>> entry : fvToSv
				.entrySet()) {
			final SingleFactVariable fv = entry.getKey();
			final Optional<VariableSymbol> symbol =
					symbols.stream().filter(vs -> vs.getEqual().getFactVariables().contains(fv)).findAny();
			map.put(fv, Pair.of(symbol.orElse(null), fvToSv.get(fv)));
		}
		return map;
	}
}
