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

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.ConditionalElementsVisitor;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Collects all symbols used within the {@link FunctionWithArguments}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <T>
 *            collection type to use while collecting the paths
 */
@Getter
public class SymbolCollector<T extends Collection<Symbol>> implements ConditionalElementsVisitor {
	private final T symbols;

	public SymbolCollector(final T symbols) {
		this.symbols = symbols;
	}

	public SymbolCollector<T> collect(final ConditionalElement ce) {
		ce.accept(this);
		return this;
	}

	public static SymbolCollector<HashSet<Symbol>> newHashSet() {
		return new SymbolCollector<>(new HashSet<Symbol>());
	}

	public static SymbolCollector<LinkedHashSet<Symbol>> newLinkedHashSet() {
		return new SymbolCollector<>(new LinkedHashSet<Symbol>());
	}

	public static SymbolCollector<ArrayList<Symbol>> newArrayList() {
		return new SymbolCollector<>(new ArrayList<Symbol>());
	}

	public static SymbolCollector<LinkedList<Symbol>> newLinkedList() {
		return new SymbolCollector<>(new LinkedList<Symbol>());
	}

	public Symbol[] getSymbolArray() {
		return toArray(symbols, Symbol[]::new);
	}

	@Override
	public void visit(final AndFunctionConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final ExistentialConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final InitialFactConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final NegatedExistentialConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final NotFunctionConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final OrFunctionConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
	}

	@Override
	public void visit(final TestConditionalElement ce) {
		ce.getChildren().forEach(c -> c.accept(this));
		ce.getFwa().accept(new FWASymbolCollector<Collection<Symbol>>(symbols));
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
