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
package org.jamocha.filter;

import java.util.HashMap;

import lombok.EqualsAndHashCode;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.filter.impls.Predicates;

import sun.security.pkcs11.wrapper.Functions;

public class TODODatenkrakeFunktionen {

	@EqualsAndHashCode
	public static class CombinedClipsAndParams {
		final String inClips;
		final SlotType[] params;

		public CombinedClipsAndParams(String inClips, SlotType[] params) {
			super();
			this.inClips = inClips;
			this.params = params;
		}

	}

	public static HashMap<CombinedClipsAndParams, Function> clipsFunctions = new HashMap<>();

	static {
		addImpl(Predicates.class);
		addImpl(Functions.class);
		// hier weitere funktionspakete angeben
	}

	public static void load() {
		try {
			Class.forName(TODODatenkrakeFunktionen.class.getName());
		} catch (ClassNotFoundException e) {
			// will never ever happen!
		}
	}

	public static void addImpl(Class<?> clazz) {
		try {
			Class.forName(clazz.getName());
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
	}

	public static void addImpl(final Function impl) {
		clipsFunctions.put(
				new CombinedClipsAndParams(impl.toString(), impl.paramTypes()),
				impl);
	}

	public static Function lookup(final String inClips,
			final SlotType... params) {
		final Function function = clipsFunctions.get(new CombinedClipsAndParams(inClips, params));
		if (function == null) throw new UnsupportedOperationException("Function \"" + inClips + "\" not loaded or implemented.");
		return function;
	}
}
