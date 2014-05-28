/*
 * Copyright 2002-2012 The Jamocha Team
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

package org.jamocha.dn.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;

/**
 * Root node implementation (not part of the {@link Node} type hierarchy).
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RootNode {

	/**
	 * Maps from {@link Template} to corresponding {@link ObjectTypeNode}
	 */
	private final Map<Template, ObjectTypeNode> templateToInput = new HashMap<>();

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} the method passed with the {@link ObjectTypeNode} and the {@link Fact} as
	 * parameters.
	 * 
	 * @param fact
	 *            {@link Fact} to be retracted
	 * @param assertOrRetract
	 *            method to be called with the corresponding {@link ObjectTypeNode}
	 */
	private void processFact(final Fact fact, final BiConsumer<ObjectTypeNode, Fact> assertOrRetract) {
		final Template template = fact.getTemplate();
		final ObjectTypeNode matchingOTN = this.templateToInput.get(template);
		assertOrRetract.accept(matchingOTN, fact);
	}

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} calling {@link ObjectTypeNode#assertFact(Fact)}.
	 * 
	 * @param fact
	 *            {@link Fact} to be asserted
	 */
	public void assertFact(final Fact fact) {
		processFact(fact, (final ObjectTypeNode otn, final Fact f) -> {
			otn.assertFact(f);
		});
	}

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} calling {@link ObjectTypeNode#retractFact(Fact)}.
	 * 
	 * @param fact
	 *            {@link Fact} to be retracted
	 */
	public void retractFact(final Fact fact) {
		processFact(fact, (final ObjectTypeNode otn, final Fact f) -> {
			otn.retractFact(f);
		});
	}

	/**
	 * Adds the given {@link ObjectTypeNode} to correspond to its {@link Template}.
	 * 
	 * @param otn
	 *            {@link ObjectTypeNode} to add
	 */
	public void putOTN(final ObjectTypeNode otn) {
		this.templateToInput.put(otn.template, otn);
	}

	/**
	 * Removes the given {@link ObjectTypeNode} from corresponding to its {@link Template}.
	 * 
	 * @param otn
	 *            {@link ObjectTypeNode} to remove
	 */
	public void removeOTN(final ObjectTypeNode otn) {
		this.templateToInput.remove(otn.template);
	}

	/**
	 * Either calls {@link ObjectTypeNode#shareNode(Path...)} or creates a new
	 * {@link ObjectTypeNode} if none existed for the template for all given paths.
	 * 
	 * @param network
	 *            network to be passed to the {@link ObjectTypeNode}
	 * @param paths
	 *            paths to add to the {@link RootNode}
	 */
	public void addPaths(final Network network, final Path... paths) {
		for (final Path path : paths) {
			final ObjectTypeNode otn = this.templateToInput.get(path.getTemplate());
			if (otn != null)
				otn.shareNode(path);
			else
				this.putOTN(new ObjectTypeNode(network, path));
		}
	}
}
