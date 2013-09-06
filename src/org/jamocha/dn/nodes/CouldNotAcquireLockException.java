/*
 * Copyright 2002-2013 The Jamocha Team
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

/**
 * Exception thrown when a lock could not be acquired, e.g. if a read-lock could not be acquired in
 * time because a write-lock stalled further read-lock-requests.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class CouldNotAcquireLockException extends Exception {

	private static final long serialVersionUID = 39352962945658122L;

	public CouldNotAcquireLockException() {
		super();
	}

	public CouldNotAcquireLockException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CouldNotAcquireLockException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CouldNotAcquireLockException(final String message) {
		super(message);
	}

	public CouldNotAcquireLockException(final Throwable cause) {
		super(cause);
	}

}
