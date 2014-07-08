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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ScopeStack {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	private static class Scope {
		final Scope parentScope;
		final HashMap<String, Symbol> symbolTable = new HashMap<>();

		public Symbol getSymbol(final String image) {
			return this.symbolTable.computeIfAbsent(image, s -> null == parentScope ? null
					: parentScope.getSymbol(s));
		}

		public Symbol getOrCreateSymbol(final String image) {
			// if no entry present, try parent
			// if no scope contains matching symbol, create it at lowest scope
			return this.symbolTable.computeIfAbsent(image, s -> Optional.ofNullable(parentScope)
					.map(c -> c.getSymbol(s)).orElseGet(() -> new Symbol(s)));
		}

		public Symbol createDummySymbol() {
			return new Symbol("Dummy");
		}
	}

	/**
	 * Wrapper class for a string without the corresponding {@link Object#equals(Object)} and
	 * {@link Object#hashCode()} functions.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@ToString(includeFieldNames = false)
	public static class Symbol {
		final String image;
	}

	private Scope currentScope;

	public ScopeStack() {
		this.currentScope = new Scope((Scope) null);
	}

	public void openScope() {
		this.currentScope = new Scope(this.currentScope);
	}

	public void closeScope() {
		this.currentScope = this.currentScope.parentScope;
	}

	private Scope getScope() {
		return Objects.requireNonNull(this.currentScope, "No scope present!");
	}

	public Symbol getOrCreate(final String image) {
		return getScope().getOrCreateSymbol(image);
	}

	public Symbol createDummy() {
		return getScope().createDummySymbol();
	}
}