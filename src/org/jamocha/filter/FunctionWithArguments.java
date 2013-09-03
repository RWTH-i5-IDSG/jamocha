/*
 * Copyright 2002-2013 The Jamocha Team
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

import java.util.ArrayList;
import java.util.Set;

import org.jamocha.engine.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

/**
 * Interface for a Function bundled with its Arguments. The Idea is to store
 * these in Filters. A Filter is constructed using the following classes
 * implementing this interface: {@link FunctionWithArgumentsComposite},
 * {@link ConstantLeaf}, {@link PathLeaf}. In doing so, we combine Functions,
 * Constants and Paths. After all Paths used have been mapped to their
 * corresponding addresses (see {@link PathTransformation}), we can transform (
 * {@link Filter#translatePath()} ) the filter to contain only
 * {@link FunctionWithArgumentsComposite}, {@link ConstantLeaf},
 * {@link ParameterLeaf}. During this step, the {@link FilterElement}s get their
 * {@link FilterElement#addressesInTarget}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArguments extends Function {

	/**
	 * Gathers the {@link Path}s used in any {@link PathLeaf}s.
	 * 
	 * @param paths
	 *            {@link Path}s used in any {@link PathLeaf}s
	 */
	public void gatherPaths(final Set<Path> paths);

	/**
	 * Translates any {@link PathLeaf}s into {@link ParameterLeaf}s.
	 * 
	 * @return {@link FunctionWithArguments} containing {@link ParameterLeaf}s
	 *         instead of any {@link PathLeaf}s
	 */
	public FunctionWithArguments translatePath(
			final ArrayList<SlotInFactAddress> addressesInTarget);

}
