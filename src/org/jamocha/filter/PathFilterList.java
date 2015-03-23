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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.nodes.Node;
import org.jamocha.visitor.Visitable;

import com.google.common.collect.Iterables;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface PathFilterList extends Visitable<PathFilterListVisitor>, Iterable<PathNodeFilterSet> {

	default Stream<PathNodeFilterSet> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Data
	@RequiredArgsConstructor
	public class PathExistentialList implements PathFilterList {
		final PathSharedListWrapper.PathSharedList purelyExistentialPart;
		final PathNodeFilterSet existentialClosure;

		public PathExistentialList(final List<PathFilterList> purelyExistentialPart,
				final PathNodeFilterSet existentialClosure) {
			this(new PathSharedListWrapper().newSharedElement(purelyExistentialPart), existentialClosure);
		}

		@Override
		public <V extends PathFilterListVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public Iterator<PathNodeFilterSet> iterator() {
			return Iterables.concat(Iterables.concat(purelyExistentialPart), existentialClosure).iterator();
		}
	}

	public static class PathSharedListWrapper {
		final List<PathSharedList> sharedSiblings = new ArrayList<>();
		Optional<Node> lowestNodeCreatedForSiblings = Optional.empty();

		public PathSharedList newSharedElement(final List<PathFilterList> filterElements) {
			final PathSharedList newSharedElement = new PathSharedList(filterElements);
			this.sharedSiblings.add(newSharedElement);
			return newSharedElement;
		}

		public PathSharedList newSharedElement() {
			final PathSharedList newSharedElement = new PathSharedList(new ArrayList<>());
			this.sharedSiblings.add(newSharedElement);
			return newSharedElement;
		}

		public PathSharedList replace(final PathSharedList filter, final List<PathFilterList> list) {
			if (!this.sharedSiblings.remove(filter)) {
				return null;
			}
			return newSharedElement(list);
		}

		@Data
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class PathSharedList implements PathFilterList {
			final List<PathFilterList> filters;

			public PathSharedListWrapper getWrapper() {
				return PathSharedListWrapper.this;
			}

			public List<PathSharedList> getSiblings() {
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
		}
	}
}
