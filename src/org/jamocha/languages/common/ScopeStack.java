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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.jamocha.visitor.Visitable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class ScopeStack {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	private class Scope {
		final HashMap<String, SymbolBean> symbolTable = new HashMap<>();
		final Scope parent;

		private SymbolBean getSymbol(final String image) {
			return this.symbolTable.getOrDefault(image,
					null == parent ? null : parent.getSymbol(image));
		}

		public SymbolBean getOrCreate(final String image) {
			{
				final SymbolBean symbolBean = this.getSymbol(image);
				if (null != symbolBean)
					return symbolBean;
			}
			final SymbolBean symbolBean = new SymbolBean(image);
			this.symbolTable.put(image, symbolBean);
			return symbolBean;
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@Setter
	public class SymbolBean implements Visitable<BeansVisitor> {
		String image;

		@Override
		public <T extends BeansVisitor> T accept(final T visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	@Delegate
	private Scope currentScope;

	public ScopeStack() {
		this.currentScope = new Scope(null);
	}

	public void pushScope() {
		this.currentScope = new Scope(currentScope);
	}

	public void popScope() {
		this.currentScope = currentScope.parent;
	}
}
