/*
 * Copyright 2002-2013 The Jamocha Team
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
import java.util.Collection;

import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

/**
 * This class bundles a {@link Function} and its arguments. The Composite Pattern has been used. An
 * argument of a function can be either a constant or a path i.e. a slot of a {@link Fact}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface FunctionWithArguments extends Function {

	/**
	 * Gathers the {@link Path paths} used in any {@link PathLeaf path leafs}.
	 * 
	 * @param paths
	 *            {@link Path paths} used in any {@link PathLeaf path leafs}
	 */
	public <T extends Collection<Path>> T gatherPaths(final T paths);

	/**
	 * Translates any {@link PathLeaf}s into {@link ParameterLeaf}s.
	 * 
	 * @param addressesInTarget
	 *            {@link ArrayList list} of {@link SlotInFactAddress addresses} valid for the target
	 *            {@link Node node} of the {@link Filter filter} used to replace {@link PathLeaf
	 *            path leafs} with {@link ParameterLeaf parameter leafs}
	 * @return {@link FunctionWithArguments function with arguments} containing
	 *         {@link ParameterLeaf parameter leafs} instead of any {@link PathLeaf path leafs}
	 */
	public FunctionWithArguments translatePath(final ArrayList<SlotInFactAddress> addressesInTarget);

}
