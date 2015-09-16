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
package org.jamocha.filter;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;
import org.jamocha.util.Lambdas;
import org.jamocha.visitor.Visitable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface PathFilterList extends Visitable<PathFilterListVisitor>, Iterable<PathNodeFilterSet> {

	default Stream<PathNodeFilterSet> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Getter
	@RequiredArgsConstructor
	public class PathExistentialList implements PathFilterList {
		@NonNull
		final Path initialPath;
		@NonNull
		final PathFilterList purePart;
		@NonNull
		final PathNodeFilterSet existentialClosure;

		@Override
		public <V extends PathFilterListVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public Iterator<PathNodeFilterSet> iterator() {
			return Iterables.concat(purePart, existentialClosure).iterator();
		}
	}

	public static PathFilterList toSimpleList(final List<PathFilterList> list) {
		if (list.size() == 1)
			return list.get(0);
		final PathSharedListWrapper pathSharedListWrapper = new PathSharedListWrapper(1);
		final PathSharedList pathSharedList = pathSharedListWrapper.sharedSiblings.get(0);
		pathSharedListWrapper.addSharedColumns(Collections.singletonMap(pathSharedList, list));
		return pathSharedList;
	}

	@Getter
	public static class PathSharedListWrapper {
		final ImmutableList<PathSharedList> sharedSiblings;

		public PathSharedListWrapper(final int ruleCount) {
			this.sharedSiblings =
					IntStream.range(0, ruleCount).mapToObj(i -> new PathSharedList(new LinkedList<>()))
							.collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
		}

		public void addSharedColumn(final Map<PathSharedList, PathFilterList> filters) {
			for (final PathSharedList sibling : sharedSiblings) {
				sibling.filters.add(filters.get(sibling));
			}
		}

		public void addSharedColumns(final Map<PathSharedList, ? extends Iterable<PathFilterList>> filters) {
			for (final PathSharedList sibling : sharedSiblings) {
				Iterables.addAll(sibling.filters, filters.get(sibling));
			}
		}

		public void clear() {
			for (final PathSharedList sibling : sharedSiblings) {
				sibling.filters.clear();
			}
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class PathSharedList implements PathFilterList {
			@NonNull
			private final LinkedList<PathFilterList> filters;

			public PathSharedListWrapper getWrapper() {
				return PathSharedListWrapper.this;
			}

			public ImmutableList<PathFilterList> getUnmodifiableFilterListCopy() {
				return ImmutableList.copyOf(filters);
			}

			public ImmutableList<PathSharedList> getSiblings() {
				return sharedSiblings;
			}

			@Override
			public <V extends PathFilterListVisitor> V accept(final V visitor) {
				visitor.visit(this);
				return visitor;
			}

			@Override
			public Iterator<PathNodeFilterSet> iterator() {
				return Iterables.concat(filters).iterator();
			}

			public ModificationProxy startModifying() {
				return new ModificationProxy();
			}

			private abstract class Proxy {
				final ImmutableMap<PathSharedList, ImmutableList<PathFilterList>> siblingToFilters = Maps.toMap(
						sharedSiblings, PathSharedList::getUnmodifiableFilterListCopy);
				final ImmutableMap<PathFilterList, Integer> chosenElementToIndex;

				private Proxy() {
					final ImmutableList<PathFilterList> chosenElements = siblingToFilters.get(PathSharedList.this);
					chosenElementToIndex =
							Maps.uniqueIndex(IntStream.range(0, chosenElements.size()).iterator(), chosenElements::get);
				}
			}

			public class ModificationProxy extends Proxy {
				public void clear() {
					PathSharedListWrapper.this.clear();
				}

				public void add(final PathFilterList oldFilter) {
					final int index = chosenElementToIndex.get(oldFilter);
					PathSharedListWrapper.this
							.addSharedColumn(Maps.transformValues(siblingToFilters, l -> l.get(index)));
				}

				public void addAll(final Iterable<? extends PathFilterList> oldFilters) {
					final List<Integer> indices =
							Lambdas.stream(oldFilters).map(chosenElementToIndex::get).collect(toList());
					final ImmutableMap<PathSharedList, Iterable<PathFilterList>> oldFilterMap =
							Maps.toMap(sharedSiblings, (final PathSharedList sibling) -> {
								final ImmutableList<PathFilterList> siblingFilters = siblingToFilters.get(sibling);
								return Iterables.transform(indices, siblingFilters::get);
							});
					PathSharedListWrapper.this.addSharedColumns(oldFilterMap);
				}
			}

			public void combine(final Iterable<? extends PathFilterList> oldFilters,
					final Function<Iterable<PathFilterList>, PathFilterList> transformer) {
				new CombinationProxy().combine(oldFilters, transformer);
			}

			public class CombinationProxy extends Proxy {
				public void combine(final Iterable<? extends PathFilterList> oldFilters,
						final Function<Iterable<PathFilterList>, PathFilterList> transformer) {
					final HashSet<PathFilterList> oldFilterSet = Sets.newHashSet(oldFilters);
					final List<Integer> indices =
							Lambdas.stream(oldFilters).map(chosenElementToIndex::get).collect(toList());
					final Map<PathSharedList, List<PathFilterList>> newFilterMap =
							Maps.toMap(
									sharedSiblings,
									(final PathSharedList sibling) -> {
										boolean inserted = false;
										final ImmutableList<PathFilterList> siblingFilters =
												siblingToFilters.get(sibling);
										final Iterable<PathFilterList> changingFilters =
												Iterables.transform(indices, siblingFilters::get);
										final List<PathFilterList> newFilterList = new ArrayList<>();
										for (final PathFilterList filter : siblingFilters) {
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
					PathSharedListWrapper.this.clear();
					PathSharedListWrapper.this.addSharedColumns(newFilterMap);
				}
			}
		}
	}
}
