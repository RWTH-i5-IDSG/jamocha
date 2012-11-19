package org.jamocha.engine.nodes;

import java.lang.ref.WeakReference;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;

public class AlphaNode extends Node {

	protected class AlphaNodeInputImpl extends NodeInputImpl {
		
		public AlphaNodeInputImpl(
				final Node shelteringNode,
				final Node parent) {
			super(shelteringNode, parent);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Message[] acceptPlusToken(final PlusToken token) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Message[] acceptMinusToken(final MinusToken token) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FactAddress getAddress(FactAddress add) {
			throw new Error("The Input of an AlphaNode is not supposed to be used as an address");
		}

	}

	public AlphaNode(final Memory memory) {
		super(memory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected NodeInputImpl newNodeInput(
			final Node parent) {
		// TODO Auto-generated method stub
		return new AlphaNodeInputImpl(this, parent);
	}

}
