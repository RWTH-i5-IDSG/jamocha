/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
public class Lambdas {

	/* composition with generic return type */

	public static <A, B, C> Function<? super A, ? extends C> compose(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b) {
		return a.andThen(b);
	}

	public static <A, B, C, D> Function<? super A, ? extends D> compose(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c) {
		return compose(compose(a, b), c);
	}

	public static <A, B, C, D, E> Function<? super A, ? extends E> compose(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final Function<? super D, ? extends E> d) {
		return compose(compose(a, b, c), d);
	}

	public static <A, B, C, D, E, F> Function<? super A, ? extends F> compose(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final Function<? super D, ? extends E> d, final Function<? super E, ? extends F> e) {
		return compose(compose(a, b, c, d), e);
	}

	/* composition with return type int */

	public static <A, B> ToIntFunction<? super A> composeToInt(final Function<? super A, ? extends B> a,
			final ToIntFunction<? super B> b) {
		return t -> b.applyAsInt(a.apply(t));
	}

	public static <A, B, C> ToIntFunction<? super A> composeToInt(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final ToIntFunction<? super C> c) {
		return composeToInt(compose(a, b), c);
	}

	public static <A, B, C, D> ToIntFunction<? super A> composeToInt(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final ToIntFunction<? super D> d) {
		return composeToInt(compose(a, b, c), d);
	}

	public static <A, B, C, D, E> ToIntFunction<? super A> composeToInt(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final Function<? super D, ? extends E> d, final ToIntFunction<? super E> e) {
		return composeToInt(compose(a, b, c, d), e);
	}

	/* composition with return type double */

	public static <A, B> ToDoubleFunction<? super A> composeToDouble(final Function<? super A, ? extends B> a,
			final ToDoubleFunction<? super B> b) {
		return t -> b.applyAsDouble(a.apply(t));
	}

	public static <A, B, C> ToDoubleFunction<? super A> composeToDouble(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final ToDoubleFunction<? super C> c) {
		return composeToDouble(compose(a, b), c);
	}

	public static <A, B, C, D> ToDoubleFunction<? super A> composeToDouble(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final ToDoubleFunction<? super D> d) {
		return composeToDouble(compose(a, b, c), d);
	}

	public static <A, B, C, D, E> ToDoubleFunction<? super A> composeToDouble(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final Function<? super D, ? extends E> d, final ToDoubleFunction<? super E> e) {
		return composeToDouble(compose(a, b, c, d), e);
	}

	/* composition with return type long */

	public static <A, B> ToLongFunction<? super A> composeToLong(final Function<? super A, ? extends B> a,
			final ToLongFunction<? super B> b) {
		return t -> b.applyAsLong(a.apply(t));
	}

	public static <A, B, C> ToLongFunction<? super A> composeToLong(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final ToLongFunction<? super C> c) {
		return composeToLong(compose(a, b), c);
	}

	public static <A, B, C, D> ToLongFunction<? super A> composeToLong(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final ToLongFunction<? super D> d) {
		return composeToLong(compose(a, b, c), d);
	}

	public static <A, B, C, D, E> ToLongFunction<? super A> composeToLong(final Function<? super A, ? extends B> a,
			final Function<? super B, ? extends C> b, final Function<? super C, ? extends D> c,
			final Function<? super D, ? extends E> d, final ToLongFunction<? super E> e) {
		return composeToLong(compose(a, b, c, d), e);
	}

	/* predicate negation */

	public static <A> Predicate<? super A> negate(final Predicate<? super A> p) {
		return p.negate();
	}

	/* computeIfAbsent helpers */

	public static <A, B> Function<A, HashSet<B>> newHashSet() {
		return x -> new HashSet<B>();
	}

	public static <A, B> Function<A, LinkedHashSet<B>> newLinkedHashSet() {
		return x -> new LinkedHashSet<B>();
	}

	public static <A, B, C> Function<A, HashMap<B, C>> newHashMap() {
		return x -> new HashMap<B, C>();
	}

	public static <A, B, C> Function<A, LinkedHashMap<B, C>> newLinkedHashMap() {
		return x -> new LinkedHashMap<B, C>();
	}

	public static <A, B> Function<A, TreeSet<B>> newTreeSet() {
		return x -> new TreeSet<B>();
	}

	public static <A, B, C> Function<A, TreeMap<B, C>> newTreeMap() {
		return x -> new TreeMap<B, C>();
	}

	public static <A, B> Function<? super A, ? extends ArrayList<B>> newArrayList() {
		return x -> new ArrayList<B>();
	}

	/*
	 * iterable to stream
	 */

	public static <T> Stream<T> stream(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/*
	 * stream as iterable
	 */
	public static <T> Iterable<T> iterable(final Stream<T> stream) {
		return stream::iterator;
	}
}
