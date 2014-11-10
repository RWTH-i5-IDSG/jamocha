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
package org.jamocha.dn.memory.javaimpl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.function.IntFunction;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class JamochaArray<T> implements RandomAccess, Iterable<T> {
	private static final int INITIAL_SIZE = 10;
	private static final Object[] EMPTY = new Object[0];

	private Object[] values;
	private int size;

	public JamochaArray() {
		this.values = EMPTY;
	}

	public JamochaArray(final int initialSize) {
		this.values = new Object[initialSize];
	}

	public JamochaArray(final JamochaArray<T> original) {
		this(original, 0, original.size);
	}

	public JamochaArray(final JamochaArray<T> original, final int initialSize) {
		this(original, 0, original.size, initialSize);
	}

	public JamochaArray(final JamochaArray<T> original, final int toExclusive, final int initialSize) {
		this(original, 0, toExclusive, initialSize);
	}

	public JamochaArray(final JamochaArray<T> original, final int fromInclusive,
			final int toExclusive, final int initialSize) {
		this(initialSize);
		System.arraycopy(original.values, fromInclusive, values, 0, toExclusive);
		this.size = toExclusive - fromInclusive;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return 0 == size;
	}

	public void add(final T value) {
		final int oldLength = values.length;
		if (oldLength == size) {
			values =
					(values == EMPTY) ? new Object[INITIAL_SIZE] : Arrays.copyOf(values,
							oldLength * 2);
		}
		values[size++] = value;
	}

	public void addAll(final JamochaArray<T> source) {
		add(source, 0, source.size);
	}

	public void add(final JamochaArray<T> source, final int fromInclusive, final int length) {
		final int oldLength = values.length;
		if (values == EMPTY) {
			values = new Object[Math.max(length, INITIAL_SIZE)];
		} else if (oldLength - size < length) {
			values = Arrays.copyOf(values, oldLength + length + INITIAL_SIZE);
		}
		System.arraycopy(source.values, fromInclusive, values, size, length);
		size += length;
	}

	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T) values[index];
	}

	public void set(final int index, final T value) {
		assert index < size;
		values[index] = value;
	}

	private T getOrNull(final int index) {
		return index < size ? get(index) : null;
	}

	public Object[] toArray() {
		return values;
	}

	public T[] toArray(final T[] target) {
		assert target.length >= size;
		System.arraycopy(values, 0, target, 0, size);
		return target;
	}

	public T[] toArray(final IntFunction<T[]> arrayCtor) {
		final T[] array = arrayCtor.apply(size);
		System.arraycopy(values, 0, array, 0, size);
		return array;
	}

	public void clear() {
		Arrays.fill(values, 0, size, null);
		size = 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int index = 0;
			T next = getOrNull(index);

			@Override
			public boolean hasNext() {
				return null != next;
			}

			@Override
			public T next() {
				final T t = next;
				next = getOrNull(++index);
				return t;
			}
		};
	}
}
