/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import org.jamocha.classloading.Loader;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotType;

/**
 * This class gathers the implemented {@link Function Functions} and provides a {@link FunctionDictionary#lookup(String,
 * SlotType...) lookup} functionality to find them identified by their string representation in CLIPS and their
 * parameter types.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see Predicate
 * @see SlotType
 */
@Log4j2
@UtilityClass
public class FunctionDictionary {

    /**
     * Gives the array of SlotType a hashCode and equals method to be usable in HashMaps.
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @Value
    private static class Params {
        SlotType[] params;
    }

    private static final HashMap<String, Map<Params, Function<?>>> CLIPS_FUNCTIONS = new HashMap<>();

    private static final HashMap<String, Map<Params, Function<?>>> VAR_ARGS_FUNCTION_CACHE = new HashMap<>();

    private static final HashMap<String, Map<Params, VarargsFunctionGenerator>> GENERATORS = new HashMap<>();
    private static final HashMap<String, Map<Params, FunctionWithSideEffectsGenerator>>
            FIXED_ARGS_GENERATORS_WITH_SIDE_EFFECTS = new HashMap<>();
    private static final HashMap<String, FunctionWithSideEffectsGenerator> VAR_ARGS_GENERATORS_WITH_SIDE_EFFECTS =
            new HashMap<>();

    static {
        Loader.loadClasses("org/jamocha/function/impls");
    }

    /**
     * Loads the {@link FunctionDictionary} class to have the java vm execute its static initializer.
     */
    public static void load() {
        addImpl(FunctionDictionary.class);
    }

    /**
     * Loads the class given to have the java vm execute its static initializer.
     *
     * @param clazz
     *         class to load
     */
    public static void addImpl(final Class<?> clazz) {
        try {
            Class.forName(clazz.getName());
        } catch (final ClassNotFoundException e) {
            log.catching(e);
        }
    }

    /**
     * Adds a {@link Function} implementation to the lookup-map.
     *
     * @param impl
     *         implementation to add
     * @return implementation added
     * @throws IllegalArgumentException
     *         if there already is an existing implementations with the same name ( {@link Function#inClips()}) and
     *         parameter types ({@link Function#getParamTypes()} ) or if the function given returns a boolean but does
     *         not derive from {@link Predicate}
     */
    public static <R, F extends Function<R>> F addImpl(final F impl) {
        checkPredicate(impl);
        if (null != CLIPS_FUNCTIONS.computeIfAbsent(impl.inClips(), x -> new HashMap<>())
                .put(new Params(impl.getParamTypes()), impl)) {
            throw new IllegalArgumentException("Function " + impl.inClips() + " already defined!");
        }
        return impl;
    }

    private static <R, F extends Function<R>> void checkPredicate(final F impl) throws IllegalArgumentException {
        if (impl.getReturnType() == SlotType.BOOLEAN && !(impl instanceof Predicate)) {
            throw new IllegalArgumentException(
                    "Functions with return type boolean have to be derived from " + "Predicate!");
        }
    }

    /**
     * Adds a generator for a {@link Function} implementation to the lookup-map. The generator will be able to generate
     * a function implementation for two or more parameters of the given {@code parameterType}.
     *
     * @param inClips
     *         CLIPS representation of the function name
     * @param parameterType
     *         type of the parameters
     * @param varargsFunctionGenerator
     *         generator to be registered
     * @throws IllegalArgumentException
     *         if there already is an existing generator for the same name ({@code IN_CLIPS}) and parameter type ({@code
     *         parameterType})
     */
    public static void addGenerator(final String inClips, final SlotType parameterType,
            final VarargsFunctionGenerator varargsFunctionGenerator) {
        if (null != GENERATORS.computeIfAbsent(inClips, x -> new HashMap<>())
                .put(new Params(new SlotType[]{parameterType}), varargsFunctionGenerator)) {
            throw new IllegalArgumentException("Function " + inClips + " already defined!");
        }
    }

    /**
     * Adds a generator for a {@link Function} implementation with side-effects to the lookup-map. The generator will be
     * able to generate a function implementation for parameters of the given {@code parameterTypes}.
     *
     * @param inClips
     *         CLIPS representation of the function name
     * @param parameterTypes
     *         types of the parameters
     * @param varargsFunctionGenerator
     *         generator to be registered
     * @throws IllegalArgumentException
     *         if there already is an existing generator for the same name ({@code IN_CLIPS}) and parameter types
     *         ({@code parameterTypes})
     */
    public static void addFixedArgsGeneratorWithSideEffects(final String inClips, final SlotType[] parameterTypes,
            final FunctionWithSideEffectsGenerator varargsFunctionGenerator) {
        if (null != FIXED_ARGS_GENERATORS_WITH_SIDE_EFFECTS.computeIfAbsent(inClips, x -> new HashMap<>())
                .put(new Params(parameterTypes), varargsFunctionGenerator)) {
            throw new IllegalArgumentException("Function " + inClips + " already defined!");
        }
    }

    /**
     * Adds a generator for a {@link Function} implementation with side-effects to the lookup-map. The generator will
     * either generate a function implementation for the parameter types passed to {@link
     * FunctionWithSideEffectsGenerator#generate(SideEffectFunctionToNetwork, SlotType[])} or return null if the
     * parameter types are incompatible.
     *
     * @param inClips
     *         CLIPS representation of the function name
     * @param varargsFunctionGenerator
     *         generator to be registered
     * @throws IllegalArgumentException
     *         if there already is an existing generator for the same name ({@code IN_CLIPS}) and parameter types
     *         ({@code parameterTypes})
     */
    public static void addVarArgsGeneratorWithSideEffects(final String inClips,
            final FunctionWithSideEffectsGenerator varargsFunctionGenerator) {
        if (null != VAR_ARGS_GENERATORS_WITH_SIDE_EFFECTS.put(inClips, varargsFunctionGenerator)) {
            throw new IllegalArgumentException("Function " + inClips + " already defined!");
        }
    }

    /**
     * Looks up an implementation for the {@link Function} identified by its string representation in CLIPS and its
     * parameter types.
     *
     * @param inClips
     *         string representation of the function in CLIPS
     * @param params
     *         parameter types
     * @return a matching @{link Function} implementation
     * @throws UnsupportedOperationException
     *         iff no {@link Function} implementation was found for the given string representation and parameter types
     */
    @SuppressWarnings("unchecked")
    public static <T> Function<T> lookup(final String inClips, final SlotType... params) {
        final Params paramTypes = new Params(params);
        {
            final Function<T> function =
                    (Function<T>) CLIPS_FUNCTIONS.getOrDefault(inClips, Collections.emptyMap()).get(paramTypes);
            if (function != null) return function;
        }
        // look for function with arbitrarily many params
        if (params.length < 2) {
            throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
        }
        // assert that all param types are the same
        for (final SlotType param : params) {
            if (param != params[0]) throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
        }
        {
            final Function<T> function =
                    (Function<T>) VAR_ARGS_FUNCTION_CACHE.getOrDefault(inClips, Collections.emptyMap()).get(paramTypes);
            if (function != null) return function;
        }
        final Params slotTypes = new Params(new SlotType[]{params[0]});
        final VarargsFunctionGenerator varargsFunctionGenerator =
                GENERATORS.getOrDefault(inClips, Collections.emptyMap()).get(slotTypes);
        if (null != varargsFunctionGenerator) {
            final Function<T> generated = (Function<T>) varargsFunctionGenerator.generate(params);
            if (null != generated) {
                checkPredicate(generated);
                VAR_ARGS_FUNCTION_CACHE.computeIfAbsent(inClips, x -> new HashMap<>()).put(paramTypes, generated);
                return generated;
            }
        }
        throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
    }

    private static String unsupportedMsg(final String inClips, final SlotType[] params) {
        return "Function \"" + inClips + "\" not loaded or implemented for argument types " + Arrays.toString(params);
    }

    /**
     * Looks up an implementation for the {@link Predicate} identified by its string representation in CLIPS and its
     * parameter types.
     *
     * @param inClips
     *         string representation of the predicate in CLIPS
     * @param params
     *         parameter types
     * @return a matching @{link Predicate} implementation
     * @throws UnsupportedOperationException
     *         iff no {@link Predicate} implementation was found for the given string representation and parameter
     *         types
     */
    public static Predicate lookupPredicate(final String inClips, final SlotType... params) {
        final Function<?> function = lookup(inClips, params);
        assert function.getReturnType() == SlotType.BOOLEAN;
        return (Predicate) function;
    }

    /**
     * Looks up an implementation for the {@link Predicate} identified by its string representation in CLIPS and its
     * parameter types.
     *
     * @param network
     *         network INSTANCE to be used to deliver the side-effects
     * @param inClips
     *         string representation of the predicate in CLIPS
     * @param params
     *         parameter types
     * @return a matching @{link Function} implementation
     * @throws UnsupportedOperationException
     *         iff no {@link Function} implementation was found for the given string representation and parameter types
     */
    @SuppressWarnings("unchecked")
    public static <T> Function<T> lookupWithSideEffects(final SideEffectFunctionToNetwork network, final String inClips,
            final SlotType... params) {
        try {
            // try without side effects
            return lookup(inClips, params);
        } catch (final UnsupportedOperationException e) {
            // try fixed argument number with side effects
            {
                final FunctionWithSideEffectsGenerator fixedArgsFunctionGenerator =
                        FIXED_ARGS_GENERATORS_WITH_SIDE_EFFECTS.computeIfAbsent(inClips, x -> new HashMap<>())
                                .get(new Params(params));
                if (null != fixedArgsFunctionGenerator) {
                    final Function<T> generated = (Function<T>) fixedArgsFunctionGenerator.generate(network, params);
                    if (null != generated) {
                        checkPredicate(generated);
                        return generated;
                    }
                }
            }
            // try variable argument number with side effects
            {
                final FunctionWithSideEffectsGenerator varargsFunctionGenerator =
                        VAR_ARGS_GENERATORS_WITH_SIDE_EFFECTS.get(inClips);
                if (null != varargsFunctionGenerator) {
                    final Function<T> generated = (Function<T>) varargsFunctionGenerator.generate(network, params);
                    if (null != generated) {
                        checkPredicate(generated);
                        return generated;
                    }
                }
            }
            throw new UnsupportedOperationException(unsupportedMsg(inClips, params));
        }
    }

    private static String formatSimpleFunction(final String inClips, final SlotType[] slotTypes) {
        final StringBuilder sb = new StringBuilder();
        sb.append('(').append(inClips);
        for (final SlotType slotType : slotTypes) {
            sb.append(' ').append(slotType);
        }
        sb.append(')');
        return sb.toString();
    }

    public static Collection<String> autoComplete(final String inClips) {
        final List<String> simpleFunctions =
                CLIPS_FUNCTIONS.entrySet().stream().filter(e -> e.getKey().startsWith(inClips)).<String>flatMap(
                        e -> e.getValue().keySet().stream()
                                .map(types -> formatSimpleFunction(e.getKey(), types.params)))
                        .collect(Collectors.toList());
        return simpleFunctions;
    }

    public static void main(final String[] args) {
        FunctionDictionary.load();
        for (final String a : autoComplete(">")) {
            System.out.println(a);
        }
    }
}
