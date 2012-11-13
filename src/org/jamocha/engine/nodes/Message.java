package org.jamocha.engine.nodes;

import java.lang.ref.WeakReference;

import org.jamocha.engine.nodes.Node.NodeInput;

public class Message {
	private final WeakReference<Node.NodeInput> nodeInput;
	private final Token token;

	public Message(final WeakReference<NodeInput> nodeInput, final Token token) {
		this.nodeInput = nodeInput;
		this.token = token;
	}

	public WeakReference<Node.NodeInput> getNodeInput() {
		return nodeInput;
	}

	public Token getToken() {
		return token;
	}

	/**
	 * @throws NullPointerException
	 *             if nodeInput has been cleared
	 */
	public void spread() {
		token.spreadTo(nodeInput.get());
	}
}
