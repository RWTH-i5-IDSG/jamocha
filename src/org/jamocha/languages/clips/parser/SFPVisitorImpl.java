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
package org.jamocha.languages.clips.parser;

import java.util.EnumSet;

import lombok.RequiredArgsConstructor;

import org.jamocha.languages.clips.parser.generated.Node;
import org.jamocha.languages.clips.parser.generated.SFPFalse;
import org.jamocha.languages.clips.parser.generated.SFPFloat;
import org.jamocha.languages.clips.parser.generated.SFPInteger;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.clips.parser.generated.SFPString;
import org.jamocha.languages.clips.parser.generated.SFPSymbol;
import org.jamocha.languages.clips.parser.generated.SFPTrue;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.Symbol;

public class SFPVisitorImpl implements SelectiveSFPVisitor {

	final ScopeStack scope = new ScopeStack();

	@Override
	public Object visit(SFPStart node, Object data) {
		return null;
	}

	@Override
	public Object visit(SFPFloat node, Object data) {
		return Double.parseDouble(node.jjtGetValue().toString());
	}

	@Override
	public Object visit(SFPInteger node, Object data) {
		return Long.parseLong(node.jjtGetValue().toString());
	}

	public <V extends SelectiveSFPVisitor, N extends Node> V sendVisitor(final V visitor,
			final N node, final Object data) {
		node.jjtAccept(visitor, data);
		return visitor;
	}

	@RequiredArgsConstructor
	static enum Type {
		Nil, DateTime, Symbol, String, Integer, Float, BooleanSymbol;

		final static EnumSet<Type> Number = EnumSet.of(Integer, Float);
		final static EnumSet<Type> Constant = EnumSet.of(Nil, DateTime, Symbol, String, Integer,
				Float, BooleanSymbol);
	}

	class SFPSymbolVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		public Object visit(SFPSymbol node, Object data) {
			this.symbol = scope.getOrCreate(node.jjtGetValue().toString());
			return data;
		};
	}

	class SFPStringVisitor implements SelectiveSFPVisitor {
		String string;

		public Object visit(SFPSymbol node, Object data) {
			this.string = node.jjtGetValue().toString();
			return data;
		};
	}

	class SFPValueVisitor implements SelectiveSFPVisitor {
		Type type;
		Object value;
		final EnumSet<Type> allowed;

		public SFPValueVisitor(final Type firstAllowed, final Type... restAllowed) {
			this.allowed = EnumSet.of(firstAllowed, restAllowed);
		}

		public SFPValueVisitor(final Type allowed) {
			this.allowed = EnumSet.of(allowed);
		}

		public SFPValueVisitor(final EnumSet<Type> allowed) {
			this.allowed = allowed;
		}

		public SFPValueVisitor() {
			this.allowed = EnumSet.allOf(Type.class);
		}

		@Override
		public Object visit(SFPFloat node, Object data) {
			if (!allowed.contains(Type.Float))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = Type.Float;
			this.value = Double.parseDouble(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(SFPInteger node, Object data) {
			if (!allowed.contains(Type.Integer))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = Type.Integer;
			this.value = Long.parseLong(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(SFPSymbol node, Object data) {
			if (!allowed.contains(Type.Symbol))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = Type.Symbol;
			this.value = sendVisitor(new SFPSymbolVisitor(), node, data).symbol;
			return data;
		}

		@Override
		public Object visit(SFPTrue node, Object data) {
			if (!allowed.contains(Type.BooleanSymbol))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = Type.BooleanSymbol;
			this.value = true;
			return data;
		}

		@Override
		public Object visit(SFPFalse node, Object data) {
			if (!allowed.contains(Type.BooleanSymbol))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = Type.BooleanSymbol;
			this.value = false;
			return data;
		}

		@Override
		public Object visit(SFPString node, Object data) {
			if (!allowed.contains(Type.String))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = Type.String;
			this.value = sendVisitor(new SFPStringVisitor(), node, data).string;
			return data;
		}
	}
}
