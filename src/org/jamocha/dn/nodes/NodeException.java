/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.dn.nodes;

public class NodeException extends Exception {

	private static final long serialVersionUID = -4977631238178038291L;

	private Node node;

	public NodeException() {
	}

	public Node getNode() {
		return node;
	}

	public NodeException(final String message, final Node n) {
		super(n + ": " + message);
		node = n;
	}

	public NodeException(final Throwable cause, final Node n) {
		super(cause);
		node = n;
	}

	public NodeException(final String message, final Throwable cause, final Node n) {
		super(n + ": " + message, cause);
		node = n;
	}

}
