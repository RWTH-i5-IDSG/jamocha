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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.visitor.Visitable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ECFilterList extends Visitable<ECFilterListVisitor> {

	public class ECNodeFilterSet extends NodeFilterSet<ECLeaf, ECFilter>implements ECFilterList {
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
		final Set<ECFilterList> purePart;
		final ECNodeFilterSet existentialClosure;

		public ECExistentialList(final boolean positive, final SingleFactVariable initialFactVariable,
				final Set<SingleFactVariable> existentialFactVariables, final Set<ECFilterList> purePart,
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
}
