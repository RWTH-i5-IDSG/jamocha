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
package org.jamocha.languages.common;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class ScopeStack {
	public static final String dummySymbolImage = "Dummy";
	final HashMap<Symbol, GlobalVariable> globalVariables = new HashMap<>();

	public interface Scope {
		Scope getParent();

		default boolean isParentOf(final Scope possibleChild) {
			if (null == possibleChild)
				return false;
			Scope current = possibleChild;
			do {
				current = current.getParent();
				if (this == current) {
					return true;
				}
			} while (null != current);
			return false;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	private static class ScopeImpl implements Scope {
		final ScopeImpl parentScope;
		final HashMap<String, Symbol> symbolTable = new HashMap<>();

		public Symbol getSymbol(final String image) {
			return this.symbolTable.computeIfAbsent(image, s -> null == parentScope ? null : parentScope.getSymbol(s));
		}

		public Symbol getOrCreateSymbol(final String image, final BiFunction<Scope, String, ? extends Symbol> ctor) {
			// if no entry present, try parent
			// if no scope contains matching symbol, create it at lowest scope
			return this.symbolTable.computeIfAbsent(image, s -> Optional.ofNullable(parentScope)
					.map(c -> c.getSymbol(s)).orElseGet(() -> ctor.apply(this, s)));
		}

		public VariableSymbol createDummySymbol(final SlotType type) {
			return new VariableSymbol(this, dummySymbolImage, type);
		}

		@Override
		public Scope getParent() {
			return parentScope;
		}
	}

	/**
	 * Wrapper class for a string providing distinguishable instances even though the contained
	 * string may be identical.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Symbol {
		@NonNull
		final String image;

		protected Symbol(@SuppressWarnings("unused") final Scope scope, final String image) {
			this(image);
		}

		@Override
		public String toString() {
			if (image.equals(dummySymbolImage)) {
				return dummySymbolImage + ':' + super.hashCode();
			}
			return image;
		}

		public boolean isDummy() {
			return this.image.equals(dummySymbolImage);
		}

		@Override
		final public boolean equals(final Object obj) {
			return this == obj;
		}

		@Override
		final public int hashCode() {
			return image.hashCode();
		}
	}

	public static class VariableSymbol extends Symbol {
		@Getter
		@Setter
		EquivalenceClass equal;

		protected VariableSymbol(final Scope scope, final String image, final SlotType type) {
			super(image);
			this.equal = new EquivalenceClass(scope, type);
		}

		protected VariableSymbol(final Scope scope, final String image) {
			super(image);
			this.equal = new EquivalenceClass(scope);
		}

		public SlotType getType() {
			return equal.getType();
		}
	}

	private ScopeImpl currentScope;

	public ScopeStack() {
		this.currentScope = new ScopeImpl((ScopeImpl) null);
	}

	public void openScope() {
		this.currentScope = new ScopeImpl(this.currentScope);
	}

	public void closeScope() {
		this.currentScope = this.currentScope.parentScope;
	}

	private ScopeImpl getScope() {
		return Objects.requireNonNull(this.currentScope, "No scope present!");
	}

	public Scope getCurrentScope() {
		return getScope();
	}

	public Symbol getOrCreateSymbol(final String image) {
		return getScope().getOrCreateSymbol(image, Symbol::new);
	}

	public VariableSymbol getOrCreateVariableSymbol(final String image, final RuleCondition rc) {
		try {
			final VariableSymbol instance = (VariableSymbol) getScope().getOrCreateSymbol(image, VariableSymbol::new);
			if (null != rc)
				rc.addSymbol(instance);
			return instance;
		} catch (final ClassCastException e) {
			log.error("expecting to create or fetch a VariableSymbol, but casting failed!");
			throw new IllegalArgumentException();
		}
	}

	public VariableSymbol getVariableSymbol(final String image) {
		try {
			return (VariableSymbol) getScope().getSymbol(image);
		} catch (final ClassCastException e) {
			log.error("expecting to create or fetch a VariableSymbol, but casting failed!");
			throw new IllegalArgumentException();
		}
	}

	public VariableSymbol createDummyFactVariable(final Template template, final RuleCondition rc,
			final Consumer<? super SingleFactVariable> consumer) {
		final VariableSymbol instance = getScope().createDummySymbol(SlotType.FACTADDRESS);
		if (null != rc)
			rc.addSymbol(instance);
		consumer.accept(new SingleFactVariable(template, instance));
		return instance;
	}

	public VariableSymbol createDummySlotVariable(final SingleFactVariable fv, final SlotAddress slot,
			final RuleCondition rc, final Consumer<? super SingleSlotVariable> consumer) {
		final VariableSymbol instance = getScope().createDummySymbol(fv.getTemplate().getSlotType(slot));
		if (null != rc)
			rc.addSymbol(instance);
		consumer.accept(fv.newSingleSlotVariable(slot, instance));
		return instance;
	}

	public GlobalVariable setOrCreateGlobalVariable(final Symbol symbol, final Object value, final SlotType type) {
		final GlobalVariable global = globalVariables.computeIfAbsent(symbol, s -> new GlobalVariable(s, value, type));
		global.setValue(value);
		return global;
	}

	public GlobalVariable getGlobalVariable(final Symbol symbol) {
		return globalVariables.get(symbol);
	}

	public Symbol getOrCreateTopLevelSymbol(final String image) {
		ScopeImpl top = currentScope;
		while (top.parentScope != null)
			top = top.parentScope;
		return top.getOrCreateSymbol(image, Symbol::new);
	}
}
