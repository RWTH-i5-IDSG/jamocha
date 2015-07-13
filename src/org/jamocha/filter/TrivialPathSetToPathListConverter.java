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

import static java.util.stream.Collectors.toList;

import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterSet.PathExistentialSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class TrivialPathSetToPathListConverter implements PathFilterSetVisitor {

	PathFilterList result;

	public static PathFilterList convert(final PathFilterSet filter) {
		return filter.accept(new TrivialPathSetToPathListConverter()).result;
	}

	@Override
	public void visit(final PathExistentialSet set) {
		this.result = new PathExistentialList(set.getInitialPath(),
				new PathFilterList.PathSharedListWrapper().newSharedElement(
						set.getPurePart().stream().map(TrivialPathSetToPathListConverter::convert).collect(toList())),
				PathNodeFilterSet.newExistentialPathNodeFilterSet(set.isPositive(), set.getExistentialPaths(),
						set.getExistentialClosure()));
	}

	@Override
	public void visit(final PathFilter filter) {
		this.result = PathNodeFilterSet.newRegularPathNodeFilterSet(filter);
	}

}
