/*
 * Copyright 2002-2014 The Jamocha Team
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

import com.google.common.collect.Iterables;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.nodes.Node;
import org.jamocha.visitor.Visitable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface PathFilterList extends Visitable<PathFilterListVisitor>, Iterable<PathFilter> {

	@Data
	@RequiredArgsConstructor
	public class PathFilterListExistential implements PathFilterList {
		final PathFilterSharedListWrapper.PathFilterSharedList nonExistentialPart;
		final PathFilter existentialClosure;

		public PathFilterListExistential(final List<PathFilterList> nonExistentialPart,
				final PathFilter existentialClosure) {
			this(new PathFilterSharedListWrapper().newSharedElement(nonExistentialPart), existentialClosure);
		}

		@Override
		public <V extends PathFilterListVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}

		@Override
		public Iterator<PathFilter> iterator() {
			return Iterables.concat(Iterables.concat(nonExistentialPart), existentialClosure).iterator();
		}
	}

	public static class PathFilterSharedListWrapper {
		final List<PathFilterSharedList> sharedSiblings = new ArrayList<>();
		Optional<Node> lowestNodeCreatedForSiblings = Optional.empty();

		public PathFilterSharedList newSharedElement(final List<PathFilterList> filterElements) {
			final PathFilterSharedList newSharedElement = new PathFilterSharedList(filterElements);
			this.sharedSiblings.add(newSharedElement);
			return newSharedElement;
		}

		public PathFilterSharedList newSharedElement() {
			final PathFilterSharedList newSharedElement = new PathFilterSharedList(new ArrayList<>());
			this.sharedSiblings.add(newSharedElement);
			return newSharedElement;
		}

		@Data
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class PathFilterSharedList implements PathFilterList {
			final List<PathFilterList> filterElements;

			public PathFilterSharedListWrapper getWrapper() {
				return PathFilterSharedListWrapper.this;
			}

			public List<PathFilterSharedList> getSiblings() {
				return sharedSiblings;
			}

			@Override
			public <V extends PathFilterListVisitor> V accept(final V visitor) {
				visitor.visit(this);
				return visitor;
			}

			@Override
			public Iterator<PathFilter> iterator() {
				return Iterables.concat(filterElements).iterator();
			}
		}
	}
}
