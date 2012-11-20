package org.jamocha.engine.nodes;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.jamocha.engine.nodes.Token.MinusToken;
import org.jamocha.engine.nodes.Token.PlusToken;

public class BetaNode extends Node {
	
	protected class BetaNodeInputImpl extends NodeInputImpl {
		
		protected final WeakHashMap<FactAddress, FactAddress> factAddresses = new WeakHashMap<>();
		protected final FactAddress factAddress = new FactAddress();

		public BetaNodeInputImpl(
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
			if (add == null) {
				return factAddress;
			}
			FactAddress returnAddress = factAddresses.get(add);
			if (returnAddress == null) {
				returnAddress = new FactAddress();
				factAddresses.put(add, returnAddress);
			}
			return returnAddress;
		}

	}

	public BetaNode(final Memory memory) {
		super(memory);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected NodeInputImpl newNodeInput(Node parent) {
		// TODO Auto-generated method stub
		return new BetaNodeInputImpl(this, parent);
	}

}