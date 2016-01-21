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

import lombok.Getter;
import org.jamocha.function.fwa.*;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;

import java.util.*;

/**
 * Collects all {@link FunctionWithArguments} in the CE splitting up equal fwas into its arguments.
 *
 * @param <T>
 * 		collection type to use while collecting the {@link FunctionWithArguments}
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FWACollector<L extends ExchangeableLeaf<L>, T extends Collection<FunctionWithArguments<L>>>
		implements DefaultConditionalElementsVisitor<L>, DefaultFunctionWithArgumentsVisitor<L> {
	@Getter
	private final T fwas;

	public FWACollector(final T symbols) {
		this.fwas = symbols;
	}

	public FWACollector<L, T> collect(final ConditionalElement<L> ce) {
		ce.accept(this);
		return this;
	}

	public FWACollector<L, T> collect(final List<ConditionalElement<L>> ces) {
		ces.forEach(this::collect);
		return this;
	}

	public static <L extends ExchangeableLeaf<L>> FWACollector<L, HashSet<FunctionWithArguments<L>>> newHashSet() {
		return new FWACollector<>(new HashSet<>());
	}

	public static FWACollector<ECLeaf, HashSet<FunctionWithArguments<ECLeaf>>> newECLeafHashSet() {
		return new FWACollector<>(new HashSet<>());
	}

	public static FWACollector<SymbolLeaf, HashSet<FunctionWithArguments<SymbolLeaf>>> newSymbolLeafHashSet() {
		return new FWACollector<>(new HashSet<>());
	}

	public static <L extends ExchangeableLeaf<L>> FWACollector<L, LinkedHashSet<FunctionWithArguments<L>>>
	newLinkedHashSet() {
		return new FWACollector<>(new LinkedHashSet<>());
	}

	public static <L extends ExchangeableLeaf<L>> FWACollector<L, ArrayList<FunctionWithArguments<L>>> newArrayList() {
		return new FWACollector<>(new ArrayList<>());
	}

	public static <L extends ExchangeableLeaf<L>> FWACollector<L, LinkedList<FunctionWithArguments<L>>> newLinkedList
			() {
		return new FWACollector<>(new LinkedList<>());
	}

	@Override
	public void defaultAction(final ConditionalElement<L> ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final TestConditionalElement<L> ce) {
		ce.getPredicateWithArguments().accept(this);
	}

	@Override
	public void defaultAction(final FunctionWithArguments<L> fwa) {
		fwas.add(fwa);
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<L> pwac) {
		if (pwac.getFunction().inClips().equals(Equals.inClips)) {
			for (final FunctionWithArguments<L> arg : pwac.getArgs()) {
				defaultAction(arg);
			}
		} else {
			defaultAction(pwac);
		}
	}
}