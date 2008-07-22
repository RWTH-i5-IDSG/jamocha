/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine;

public class StopCompileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean subSucc;

	public StopCompileException() {
		// TODO Auto-generated constructor stub
	}

	public StopCompileException(boolean subSuccess) {
		subSucc = subSuccess;
	}

	public boolean isSubSuccessed() {
		return subSucc;
	}

	public StopCompileException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StopCompileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public StopCompileException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
