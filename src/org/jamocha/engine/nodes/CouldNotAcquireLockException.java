/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.engine.nodes;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class CouldNotAcquireLockException extends Exception {

	private static final long serialVersionUID = 39352962945658122L;

	public CouldNotAcquireLockException() {
		super();
	}

	public CouldNotAcquireLockException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CouldNotAcquireLockException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouldNotAcquireLockException(String message) {
		super(message);
	}

	public CouldNotAcquireLockException(Throwable cause) {
		super(cause);
	}

}