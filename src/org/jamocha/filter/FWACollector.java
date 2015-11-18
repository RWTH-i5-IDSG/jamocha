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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition;

/**
 * Collects all {@link FunctionWithArguments} in the CE splitting up equal fwas into its arguments.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <T>
 *            collection type to use while collecting the {@link FunctionWithArguments}
 */
public class FWACollector<T extends Collection<FunctionWithArguments<SymbolLeaf>>> implements
		DefaultConditionalElementsVisitor<SymbolLeaf>, DefaultFunctionWithArgumentsVisitor<SymbolLeaf> {
	@Getter
	private final T fwas;

	public FWACollector(final T symbols) {
		this.fwas = symbols;
	}

	public FWACollector<T> collect(final ConditionalElement<SymbolLeaf> ce) {
		ce.accept(this);
		return this;
	}

	public FWACollector<T> collect(final RuleCondition rc) {
		return collect(rc.getConditionalElements());
	}

	public FWACollector<T> collect(final List<ConditionalElement<SymbolLeaf>> ces) {
		ces.forEach(this::collect);
		return this;
	}

	public static FWACollector<HashSet<FunctionWithArguments<SymbolLeaf>>> newHashSet() {
		return new FWACollector<>(new HashSet<>());
	}

	public static FWACollector<LinkedHashSet<FunctionWithArguments<SymbolLeaf>>> newLinkedHashSet() {
		return new FWACollector<>(new LinkedHashSet<>());
	}

	public static FWACollector<ArrayList<FunctionWithArguments<SymbolLeaf>>> newArrayList() {
		return new FWACollector<>(new ArrayList<>());
	}

	public static FWACollector<LinkedList<FunctionWithArguments<SymbolLeaf>>> newLinkedList() {
		return new FWACollector<>(new LinkedList<>());
	}

	@Override
	public void defaultAction(final ConditionalElement<SymbolLeaf> ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final TestConditionalElement<SymbolLeaf> ce) {
		ce.getPredicateWithArguments().accept(this);
	}

	@Override
	public void defaultAction(final FunctionWithArguments<SymbolLeaf> fwa) {
		fwas.add(fwa);
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> pwac) {
		if (pwac.getFunction().inClips().equals(Equals.inClips)) {
			for (final FunctionWithArguments<SymbolLeaf> arg : pwac.getArgs()) {
				defaultAction(arg);
			}
		} else {
			defaultAction(pwac);
		}
	}
}