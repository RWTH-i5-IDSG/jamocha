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
package org.jamocha.filter;

import com.google.common.collect.*;
import lombok.*;
import org.jamocha.filter.ECFilterList.ECSharedListWrapper.ECSharedList;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.util.Lambdas;
import org.jamocha.visitor.Visitable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ECFilterList extends Visitable<ECFilterListVisitor> {

	public class ECNodeFilterSet extends NodeFilterSet<ECLeaf, ECFilter> implements ECFilterList {
		public ECNodeFilterSet(final Set<ECFilter> filters) {
			super(filters);
		}

		public ECNodeFilterSet(final ECFilter... filters) {
			this(new HashSet<>(Arrays.asList(filters)));
		}

		@Override
		public <V extends ECFilterListVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	@Data
	@RequiredArgsConstructor
	public class ECExistentialList implements ECFilterList {
		final SingleFactVariable initialFactVariable;
		final Set<SingleFactVariable> positiveExistentialFactVariables, negativeExistentialFactVariables;
		final ECFilterList purePart;
		final ECNodeFilterSet existentialClosure;

		public ECExistentialList(final boolean positive, final SingleFactVariable initialFactVariable,
				final Set<SingleFactVariable> existentialFactVariables, final ECFilterList purePart,
				final ECNodeFilterSet existentialClosure) {
			this(initialFactVariable, positive ? existentialFactVariables : Collections.emptySet(),
					positive ? Collections.emptySet() : existentialFactVariables, purePart, existentialClosure);
		}

		@Override
		public <V extends ECFilterListVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static ECFilterList toSimpleList(final List<ECFilterList> list) {
		if (list.size() == 1) return list.get(0);
		final ECSharedListWrapper pathSharedListWrapper = new ECSharedListWrapper(1);
		final ECSharedList pathSharedList = pathSharedListWrapper.sharedSiblings.get(0);
		pathSharedListWrapper.addSharedColumns(Collections.singletonMap(pathSharedList, list));
		return pathSharedList;
	}

	@Getter
	public static class ECSharedListWrapper {
		final ImmutableList<ECSharedList> sharedSiblings;

		public ECSharedListWrapper(final int ruleCount) {
			this.sharedSiblings = IntStream.range(0, ruleCount).mapToObj(i -> new ECSharedList(new LinkedList<>()))
					.collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
		}

		public void addSharedColumn(final Map<ECSharedList, ECFilterList> filters) {
			for (final ECSharedList sibling : sharedSiblings) {
				sibling.filters.add(filters.get(sibling));
			}
		}

		public void addSharedColumns(final Map<ECSharedList, ? extends Iterable<ECFilterList>> filters) {
			for (final ECSharedList sibling : sharedSiblings) {
				Iterables.addAll(sibling.filters, filters.get(sibling));
			}
		}

		public void clear() {
			for (final ECSharedList sibling : sharedSiblings) {
				sibling.filters.clear();
			}
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class ECSharedList implements ECFilterList {
			@NonNull
			final LinkedList<ECFilterList> filters;

			public ECSharedListWrapper getWrapper() {
				return ECSharedListWrapper.this;
			}

			public ImmutableList<ECFilterList> getUnmodifiableFilterListCopy() {
				return ImmutableList.copyOf(filters);
			}

			public ImmutableList<ECSharedList> getSiblings() {
				return sharedSiblings;
			}

			@Override
			public <V extends ECFilterListVisitor> V accept(final V visitor) {
				visitor.visit(this);
				return visitor;
			}

			public ModificationProxy startModifying() {
				return new ModificationProxy();
			}

			private abstract class Proxy {
				final ImmutableMap<ECSharedList, ImmutableList<ECFilterList>> siblingToFilters =
						Maps.toMap(sharedSiblings, ECSharedList::getUnmodifiableFilterListCopy);
				final ImmutableMap<ECFilterList, Integer> chosenElementToIndex;

				private Proxy() {
					final ImmutableList<ECFilterList> chosenElements = siblingToFilters.get(ECSharedList.this);
					chosenElementToIndex =
							Maps.uniqueIndex(IntStream.range(0, chosenElements.size()).iterator(),
									chosenElements::get);
				}
			}

			public class ModificationProxy extends Proxy {
				public void clear() {
					ECSharedListWrapper.this.clear();
				}

				public void add(final ECFilterList oldFilter) {
					final int index = chosenElementToIndex.get(oldFilter);
					ECSharedListWrapper.this.addSharedColumn(Maps.transformValues(siblingToFilters, l -> l.get
							(index)));
				}

				public void addAll(final Iterable<? extends ECFilterList> oldFilters) {
					final List<Integer> indices =
							Lambdas.stream(oldFilters).map(chosenElementToIndex::get).collect(toList());
					final ImmutableMap<ECSharedList, Iterable<ECFilterList>> oldFilterMap =
							Maps.toMap(sharedSiblings, (final ECSharedList sibling) -> {
								final ImmutableList<ECFilterList> siblingFilters = siblingToFilters.get(sibling);
								return Iterables.transform(indices, siblingFilters::get);
							});
					ECSharedListWrapper.this.addSharedColumns(oldFilterMap);
				}
			}

			public void combine(final Iterable<? extends ECFilterList> oldFilters,
					final Function<Iterable<ECFilterList>, ECFilterList> transformer) {
				new CombinationProxy().combine(oldFilters, transformer);
			}

			public class CombinationProxy extends Proxy {
				public void combine(final Iterable<? extends ECFilterList> oldFilters,
						final Function<Iterable<ECFilterList>, ECFilterList> transformer) {
					final HashSet<ECFilterList> oldFilterSet = Sets.newHashSet(oldFilters);
					final List<Integer> indices =
							Lambdas.stream(oldFilters).map(chosenElementToIndex::get).collect(toList());
					final Map<ECSharedList, List<ECFilterList>> newFilterMap =
							Maps.toMap(sharedSiblings, (final ECSharedList sibling) -> {
								boolean inserted = false;
								final ImmutableList<ECFilterList> siblingFilters = siblingToFilters.get(sibling);
								final Iterable<ECFilterList> changingFilters =
										Iterables.transform(indices, siblingFilters::get);
								final List<ECFilterList> newFilterList = new ArrayList<>();
								for (final ECFilterList filter : siblingFilters) {
									if (!oldFilterSet.contains(filter)) {
										newFilterList.add(filter);
										continue;
									}
									if (!inserted) {
										newFilterList.add(transformer.apply(changingFilters));
									}
									inserted = true;
								}
								return newFilterList;
							});
					ECSharedListWrapper.this.clear();
					ECSharedListWrapper.this.addSharedColumns(newFilterMap);
				}
			}
		}
	}
}
