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

import java.util.Arrays;
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

	private static final HashMap<CombinedClipsAndParams, Function<?>> clipsFunctions =
			new HashMap<>();
	private static final HashMap<CombinedClipsAndParams, VarargsFunctionGenerator> generators =
			new HashMap<>();
	private static final HashMap<CombinedClipsAndParams, Function<?>> clipsFunctionsWithSideEffects =
			new HashMap<>();
	private static final HashMap<CombinedClipsAndParams, VarargsFunctionGenerator> generatorsWithSideEffects =
			new HashMap<>();

	static {
		addImpl(Predicates.class);
		addImpl(Functions.class);
		// specify further function packages here
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
		} catch (final ClassNotFoundException e) {
			System.err.println(e);
		}
	}

	private static <R, F extends Function<R>> F addImpl(
			final HashMap<CombinedClipsAndParams, Function<?>> clipsFunctions, final F impl) {
		if (null != clipsFunctions.put(
				new CombinedClipsAndParams(impl.inClips(), impl.getParamTypes()), impl)) {
			throw new IllegalArgumentException("Function " + impl.inClips() + " already defined!");
		}
		return impl;
	}

	/**
	 * Adds a {@link Function} implementation to the lookup-map (will overwrite existing
	 * implementations with the same name and argument types).
	 * 
	 * @param impl
	 *            implementation to add
	 * @return implementation to add
	 */
	public static <R, F extends Function<R>> F addImpl(final F impl) {
		return addImpl(clipsFunctions, impl);
	}

	/**
	 * Adds a {@link Function} implementation to the lookup-map (will overwrite existing
	 * implementations with the same name and argument types).
	 * 
	 * @param impl
	 *            implementation to add
	 * @return implementation to add
	 */
	public static <R, F extends Function<R>> F addImplWithSideEffects(final F impl) {
		return addImpl(clipsFunctionsWithSideEffects, impl);
	}

	private static void addGenerator(
			final HashMap<CombinedClipsAndParams, VarargsFunctionGenerator> generators,
			final String string, final SlotType types,
			final VarargsFunctionGenerator varargsFunctionGenerator) {
		if (null != generators.put(new CombinedClipsAndParams(string, new SlotType[] { types }),
				varargsFunctionGenerator)) {
			throw new IllegalArgumentException("Function " + string + " already defined!");
		}
	}

	public static void addGenerator(final String string, final SlotType types,
			final VarargsFunctionGenerator varargsFunctionGenerator) {
		addGenerator(generators, string, types, varargsFunctionGenerator);
	}

	public static void addGeneratorWithSideEffects(final String string, final SlotType types,
			final VarargsFunctionGenerator varargsFunctionGenerator) {
		addGenerator(generatorsWithSideEffects, string, types, varargsFunctionGenerator);
	}

	@SuppressWarnings("unchecked")
	private static <T> Function<T> lookup(
			final HashMap<CombinedClipsAndParams, Function<?>> clipsFunctions,
			final HashMap<CombinedClipsAndParams, VarargsFunctionGenerator> generators,
			final String inClips, final SlotType... params) {
		final Function<T> function =
				(Function<T>) clipsFunctions.get(new CombinedClipsAndParams(inClips, params));
		if (function != null)
			return function;
		// look for function with arbitrarily many params
		if (params.length < 2) {
			throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
		}
		// assert that all param types are the same
		for (final SlotType param : params) {
			if (param != params[0])
				throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
		}
		final VarargsFunctionGenerator varargsFunctionGenerator =
				generators.get(new CombinedClipsAndParams(inClips, new SlotType[] { params[0] }));
		if (null != varargsFunctionGenerator) {
			final Function<T> generated = (Function<T>) varargsFunctionGenerator.generate(params);
			if (null != generated) {
				return addImpl(generated);
			}
		}
		throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
	}

	/**
	 * Looks up an implementation for the {@link Function} identified by its string representation
	 * in CLIPS and its parameter types.
	 * 
	 * @param T
	 *            return type of the function to look up
	 * @param inClips
	 *            string representation of the function in CLIPS
	 * @param params
	 *            parameter types
	 * @return a matching @{link Function} implementation
	 * @throws UnsupportedOperationException
	 *             iff no {@link Function} implementation was found for the given string
	 *             representation and parameter types
	 */
	public static <T> Function<T> lookup(final String inClips, final SlotType... params) {
		return lookup(clipsFunctions, generators, inClips, params);
	}

	private static String unsupportedMsg(final String inClips, final SlotType[] params) {
		return "Function \"" + inClips + "\" not loaded or implemented for argument types "
				+ Arrays.toString(params);
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

	public static <T> Function<T> lookupWithSideEffects(final String inClips,
			final SlotType... params) {
		try {
			return lookup(clipsFunctionsWithSideEffects, generatorsWithSideEffects, inClips, params);
		} catch (UnsupportedOperationException e) {
			return lookup(clipsFunctions, generators, inClips, params);
		}
	}
}
