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

import java.util.Set;

import org.jamocha.visitor.Visitable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface PathFilterSet extends Visitable<PathFilterSetVisitor> {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	@Getter
	public static class PathExistentialSet implements PathFilterSet {
		final boolean positive;
		final Path initialPath;
		final Set<Path> existentialPaths;
		final Set<PathFilterSet> purePart;
		final PathFilter existentialClosure;

		@Override
		public <V extends PathFilterSetVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}
}
