/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.languages.clips.parser.errors;

import lombok.Getter;

import org.jamocha.languages.clips.parser.generated.SimpleNode;
import org.jamocha.languages.common.errors.SideEffectsDisallowedHereError;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ClipsSideEffectsDisallowedHereError extends SideEffectsDisallowedHereError {
	private static final long serialVersionUID = -5181850881010979976L;

	@Getter
	final SimpleNode context;

	public ClipsSideEffectsDisallowedHereError(final String methodWithSideEffects,
			final SimpleNode context) {
		super(methodWithSideEffects);
		this.context = context;
	}

	public ClipsSideEffectsDisallowedHereError(final String methodWithSideEffects,
			final SimpleNode context, final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(methodWithSideEffects, message, cause, enableSuppression, writableStackTrace);
		this.context = context;
	}

	public ClipsSideEffectsDisallowedHereError(final String methodWithSideEffects,
			final SimpleNode context, final String message, final Throwable cause) {
		super(methodWithSideEffects, message, cause);
		this.context = context;
	}

	public ClipsSideEffectsDisallowedHereError(final String methodWithSideEffects,
			final SimpleNode context, final String message) {
		super(methodWithSideEffects, message);
		this.context = context;
	}

	public ClipsSideEffectsDisallowedHereError(final String methodWithSideEffects,
			final SimpleNode context, final Throwable cause) {
		super(methodWithSideEffects, cause);
		this.context = context;
	}
}
