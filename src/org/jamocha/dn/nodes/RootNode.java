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
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RootNode {

	private final Map<Template, ObjectTypeNode> templateToInput = new HashMap<>();

	private void processFact(final Fact fact, final BiConsumer<ObjectTypeNode, Fact> assertOrRetract)
			throws InterruptedException {
		final Template template = fact.getTemplate();
		final ObjectTypeNode matchingOTN = this.templateToInput.get(template);
		assertOrRetract.accept(matchingOTN, fact);
	}

	public void assertFact(final Fact fact) throws InterruptedException {
		processFact(fact, (final ObjectTypeNode otn, final Fact f) -> {
			otn.assertFact(f);
		});
	}

	public void retractFact(final Fact fact) throws InterruptedException {
		processFact(fact, (final ObjectTypeNode otn, final Fact f) -> {
			otn.retractFact(f);
		});
	}

	public void putOTN(final ObjectTypeNode otn) {
		this.templateToInput.put(otn.template, otn);
	}

	public void removeOTN(final ObjectTypeNode otn) {
		this.templateToInput.remove(otn.template);
	}

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
