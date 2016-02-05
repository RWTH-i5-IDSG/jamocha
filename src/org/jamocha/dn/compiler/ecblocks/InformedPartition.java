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
 * the specific language governing permissions and limitations under
 * the License.
 */

package org.jamocha.dn.compiler.ecblocks;

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.InformedPartition.InformedSubSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.jamocha.util.Lambdas.newIdentityHashSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class InformedPartition<T, I, S extends InformedSubSet<T, I>> extends Partition<T, S> {
	public static class InformedSubSet<T, I> extends Partition.SubSet<T> {
		final I info;

		public InformedSubSet(final IdentityHashMap<RowIdentifier, T> elements, final I info) {
			super(elements);
			this.info = info;
		}

		public InformedSubSet(final InformedSubSet<T, I> copy) {
			super(copy);
			this.info = copy.info;
		}

		public InformedSubSet(final Map<RowIdentifier, ? extends T> elements, final I info) {
			this(new IdentityHashMap<>(elements), info);
		}
	}

	final IdentityHashMap<I, Set<InformedSubSet<T, I>>> filterLookup = new IdentityHashMap<>();

	public InformedPartition(final InformedPartition<T, I, S> copy, final Function<S, S> copyCtor) {
		super(copy, copyCtor);
		this.filterLookup.putAll(copy.filterLookup);
	}

	@Override
	public void add(final S informedSubSet) {
		super.add(informedSubSet);
		this.filterLookup.computeIfAbsent(informedSubSet.info, newIdentityHashSet()).add(informedSubSet);
	}

	@Override
	public boolean remove(final S informedSubSet) {
		if (!super.remove(informedSubSet)) return false;
		this.filterLookup.compute(informedSubSet.info, (t, s) -> {
			s.remove(informedSubSet);
			return s.isEmpty() ? null : s;
		});
		return true;
	}
}
