package org.jamocha.engine.nodes;

import java.util.Collections;
import java.util.Set;

import org.jamocha.engine.nodes.Node.NodeInput;

abstract public class Token {
	protected final Set<FactTuple> factTuples;

	public Token(final Set<FactTuple> factTuples) {
		this.factTuples = Collections.unmodifiableSet(factTuples);
	}

	abstract public Message[] spreadTo(final Node.NodeInput nodeInput);

	abstract public Token negate();

	public Set<FactTuple> getFactTuples() {
		return this.factTuples;
	}

	public static class PlusToken extends Token {
		public PlusToken(final Set<FactTuple> factTuples) {
			super(factTuples);
		}

		@Override
		public Message[] spreadTo(final NodeInput nodeInput) {
			return nodeInput.acceptPlusToken(this);
		}

		@Override
		public MinusToken negate() {
			return new MinusToken(this.factTuples);
		}
	}

	public static class MinusToken extends Token {
		public MinusToken(Set<FactTuple> factTuples) {
			super(factTuples);
		}

		@Override
		public Message[] spreadTo(final NodeInput nodeInput) {
			return nodeInput.acceptMinusToken(this);
		}

		@Override
		public PlusToken negate() {
			return new PlusToken(this.factTuples);
		}
	}
}
