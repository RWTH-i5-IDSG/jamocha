package org.jamocha.rete.nodes;

public class NodeException extends Exception {

	private Node node;
	
	public NodeException() {
	}

	public Node getNode() {
		return node;
	}
	
	public NodeException(String message, Node n) {
		super(n+": "+message);
		this.node = n;
	}

	public NodeException(Throwable cause, Node n) {
		super(cause);
		this.node = n;
	}

	public NodeException(String message, Throwable cause, Node n) {
		super(n+": "+message, cause);
		this.node = n;
	}

}
