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
package org.jamocha.filter;

import java.util.HashMap;

import lombok.Value;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.impls.Functions;
import org.jamocha.filter.impls.Predicates;

/**
 * This class gathers the implemented {@link Function Functions} and provides a
 * {@link FunctionDictionary#lookup(String, SlotType...) lookup} functionality to find them
 * identified by their string representation in CLIPS and their parameter types.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see Predicate
 * @see SlotType
 */
public class FunctionDictionary {

	/**
	 * This class combines the CLIPS string representation (e.g. the name) of a {@link Function} and
	 * their parameter {@link SlotType types}. It is used as the key in the lookup map to find
	 * corresponding implementations.
	 * 
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Value
	private static class CombinedClipsAndParams {
		String inClips;
		SlotType[] params;
	}

	public static HashMap<CombinedClipsAndParams, Function<?>> clipsFunctions = new HashMap<>();

	static {
		addImpl(Predicates.class);
		addImpl(Functions.class);
		// hier weitere funktionspakete angeben
	}

	/**
	 * Loads the {@link FunctionDictionary} class to have the java vm execute its static
	 * initializer.
	 */
	public static void load() {
		addImpl(FunctionDictionary.class);
	}

	/**
	 * Loads the class given to have the java vm execute its static initializer.
	 * 
	 * @param clazz
	 *            class to load
	 */
	public static void addImpl(final Class<?> clazz) {
		try {
			Class.forName(clazz.getName());
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
	}

	/**
	 * Adds a {@link Function} implementation to the lookup-map (will overwrite existing
	 * implementations with the same name and argument types).
	 * 
	 * @param impl
	 *            implementation to add
	 */
	public static void addImpl(final Function<?> impl) {
		clipsFunctions.put(new CombinedClipsAndParams(impl.toString(), impl.getParamTypes()), impl);
	}

	/**
	 * Looks up an implementation for the {@link Function} identified by its string representation
	 * in CLIPS and its parameter types.
	 * 
	 * @param inClips
	 *            string representation of the function in CLIPS
	 * @param params
	 *            parameter types
	 * @return a matching @{link Function} implementation
	 * @throws UnsupportedOperationException
	 *             iff no {@link Function} implementation was found for the given string
	 *             representation and parameter types
	 */
	@SuppressWarnings("unchecked")
	public static <T> Function<T> lookup(final String inClips, final SlotType... params) {
		final Function<?> function =
				clipsFunctions.get(new CombinedClipsAndParams(inClips, params));
		if (function == null)
			throw new UnsupportedOperationException("Function \"" + inClips
					+ "\" not loaded or implemented.");
		return (Function<T>) function;
	}

	/**
	 * Looks up an implementation for the {@link Predicate} identified by its string representation
	 * in CLIPS and its parameter types.
	 * 
	 * @param inClips
	 *            string representation of the predicate in CLIPS
	 * @param params
	 *            parameter types
	 * @return a matching @{link Predicate} implementation
	 * @throws UnsupportedOperationException
	 *             iff no {@link Predicate} implementation was found for the given string
	 *             representation and parameter types
	 */
	public static Predicate lookupPredicate(final String inClips, final SlotType... params) {
		final Function<?> function = lookup(inClips, params);
		assert function.getReturnType() == SlotType.BOOLEAN;
		return (Predicate) function;
	}
}
