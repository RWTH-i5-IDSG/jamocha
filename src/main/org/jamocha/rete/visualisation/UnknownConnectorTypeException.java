/*
 * Copyright 2007 Josef Alexander Hahn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.visualisation;

public class UnknownConnectorTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownConnectorTypeException() {
		super();
	}

	public UnknownConnectorTypeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnknownConnectorTypeException(String arg0) {
		super(arg0);
	}

	public UnknownConnectorTypeException(Throwable arg0) {
		super(arg0);
	}

}
