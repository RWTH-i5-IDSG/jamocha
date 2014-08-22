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
import static java.util.stream.Collectors.toCollection;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * Collects all symbols used within the {@link FunctionWithArguments}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <T>
 *            collection type to use while collecting the paths
 */
public class SymbolCollector<T extends Collection<Symbol>, U extends Collection<SingleSlotVariable>>
		implements DefaultConditionalElementsVisitor {
	@Getter
	private final T symbols;
	private final Supplier<U> supplier;

	public SymbolCollector(final T symbols, final Supplier<U> supplier) {
		this.symbols = symbols;
		this.supplier = supplier;
	}

	public SymbolCollector<T, U> collect(final ConditionalElement ce) {
		ce.accept(this);
		return this;
	}

	public SymbolCollector<T, U> collect(final RuleCondition rc) {
		return collect(rc.getConditionalElements());
	}

	public SymbolCollector<T, U> collect(final List<ConditionalElement> ces) {
		ces.forEach(this::collect);
		return this;
	}

	public static SymbolCollector<HashSet<Symbol>, HashSet<SingleSlotVariable>> newHashSet() {
		return new SymbolCollector<>(new HashSet<Symbol>(), HashSet::new);
	}

	public static SymbolCollector<LinkedHashSet<Symbol>, LinkedHashSet<SingleSlotVariable>> newLinkedHashSet() {
		return new SymbolCollector<>(new LinkedHashSet<Symbol>(), LinkedHashSet::new);
	}

	public static SymbolCollector<ArrayList<Symbol>, ArrayList<SingleSlotVariable>> newArrayList() {
		return new SymbolCollector<>(new ArrayList<Symbol>(), ArrayList::new);
	}

	public static SymbolCollector<LinkedList<Symbol>, LinkedList<SingleSlotVariable>> newLinkedList() {
		return new SymbolCollector<>(new LinkedList<Symbol>(), LinkedList::new);
	}

	public Symbol[] getSymbolArray() {
		return toArray(symbols, Symbol[]::new);
	}

	private Stream<SingleSlotVariable> getSlotVariableStream() {
		return this.symbols.stream().flatMap(
				symbol -> Stream.concat(symbol.getPositiveSlotVariables().stream(), symbol
						.getNegativeSlotVariables().stream()));
	}

	public Map<SingleFactVariable, List<SingleSlotVariable>> toSlotVariablesByFactVariable() {
		return getSlotVariableStream().collect(
				groupingBy((final SingleSlotVariable ssv) -> ssv.getFactVariable()));
	}

	public U toSlotVariables() {
		return getSlotVariableStream().collect(toCollection(supplier));
	}

	public SingleSlotVariable[] toSlotVariableArray() {
		return toArray(toSlotVariables(), SingleSlotVariable[]::new);
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final TestConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
		ce.getPredicateWithArguments().accept(new FWASymbolCollector<Collection<Symbol>>(symbols));
	}

	@Override
	public void visit(final TemplatePatternConditionalElement ce) {
		final Symbol symbol = ce.getFactVariable().getSymbol();
		if (ScopeStack.dummySymbolImage.equals(symbol.getImage()))
			return;
		symbols.add(symbol);
	}

	@AllArgsConstructor
	@Getter
	private static class FWASymbolCollector<T extends Collection<Symbol>> implements
			FunctionWithArgumentsVisitor {
		final T symbols;

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			for (final FunctionWithArguments fwa : functionWithArgumentsComposite.getArgs()) {
				fwa.accept(this);
			}
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			for (final FunctionWithArguments fwa : predicateWithArgumentsComposite.getArgs()) {
				fwa.accept(this);
			}
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
		}

		@Override
		public void visit(final Assert fwa) {
			for (final FunctionWithArguments child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final Modify fwa) {
			fwa.getTargetFact().accept(this);
			for (final SlotAndValue child : fwa.getArgs()) {
				child.getValue().accept(this);
			}
		}

		@Override
		public void visit(final Modify.SlotAndValue fwa) {
			fwa.getValue().accept(this);
		}

		@Override
		public void visit(final Retract fwa) {
			for (final FunctionWithArguments child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final SymbolLeaf fwa) {
			this.symbols.add(fwa.getSymbol());
		}

		@Override
		public void visit(final Assert.TemplateContainer fwa) {
			for (final FunctionWithArguments child : fwa.getArgs()) {
				child.accept(this);
			}
		}
	}
}
