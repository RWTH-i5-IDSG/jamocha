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

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class ScopeStack {

	interface ScopeI {
		public Symbol getOrCreate(final String image);

		public Symbol createDummy();
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	private static class Scope implements ScopeI {
		final HashMap<String, Symbol> symbolTable = new HashMap<>();

		@Override
		public Symbol getOrCreate(final String image) {
			return this.symbolTable.computeIfAbsent(image, Symbol::new);
		}

		@Override
		public Symbol createDummy() {
			return new Symbol("Dummy");
		}
	}

	private static class NoScope implements ScopeI {
		@Override
		public Symbol getOrCreate(String image) {
			throw new UnsupportedOperationException("No Scope present!");
		}

		@Override
		public Symbol createDummy() {
			throw new UnsupportedOperationException("No Scope present!");
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

	private ScopeI currentScope;

	public ScopeStack() {
		this.currentScope = new NoScope();
	}

	public void openScope() {
		this.currentScope = new Scope();
	}

	public void closeScope() {
		this.currentScope = new NoScope();
	}

	public Symbol getOrCreate(final String image) {
		return this.currentScope.getOrCreate(image);
	}

	public Symbol createDummy() {
		return this.currentScope.createDummy();
	}
}
