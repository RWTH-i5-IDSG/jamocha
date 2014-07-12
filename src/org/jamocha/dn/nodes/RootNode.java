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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
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
	private final Map<Template, ObjectTypeNode> templateToOTN = new HashMap<>();

	private final Map<Integer, MemoryFact> facts = new HashMap<>();
	private int factIdentifierCounter = 0;

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} calling {@link ObjectTypeNode#assertFact(Fact)}.
	 * 
	 * @param fact
	 *            {@link Fact} to be asserted
	 */
	public FactIdentifier[] assertFact(final Fact... facts) {
		final int length = facts.length;
		final FactIdentifier[] factIdentifiers = new FactIdentifier[length];
		final Map<Fact, Integer> orderCache = new HashMap<>();
		for (int i = 0; i < length; ++i) {
			final int id = ++factIdentifierCounter;
			factIdentifiers[i] = new FactIdentifier(id);
			orderCache.put(facts[i], id);
		}
		Arrays.stream(facts).collect(groupingBy(Fact::getTemplate)).forEach((t, fl) -> {
			final Fact[] fs = toArray(fl, Fact[]::new);
			final MemoryFact[] mfs = this.templateToOTN.get(t).assertFact(fs);
			IntStream.range(0, mfs.length).forEach(i -> {
				final MemoryFact mf = mfs[i];
				final Fact f = fs[i];
				this.facts.put(orderCache.get(f), mf);
			});
		});
		return factIdentifiers;
	}

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} calling {@link ObjectTypeNode#retractFact(Fact)}.
	 * 
	 * @param fact
	 *            {@link Fact} to be retracted
	 */
	private void retractFact(final List<MemoryFact> facts) {
		facts.stream()
				.collect(groupingBy(f -> f.getTemplate()))
				.forEach(
						(t, f) -> this.templateToOTN.get(t).retractFact(
								toArray(f, MemoryFact[]::new)));
	}

	public void retractFact(FactIdentifier... factIdentifiers) {
		retractFact(Arrays.stream(factIdentifiers).map(i -> facts.get(i.getId())).collect(toList()));
	}

	/**
	 * Adds the given {@link ObjectTypeNode} to correspond to its {@link Template}.
	 * 
	 * @param otn
	 *            {@link ObjectTypeNode} to add
	 */
	public void putOTN(final ObjectTypeNode otn) {
		this.templateToOTN.put(otn.template, otn);
	}

	/**
	 * Removes the given {@link ObjectTypeNode} from corresponding to its {@link Template}.
	 * 
	 * @param otn
	 *            {@link ObjectTypeNode} to remove
	 */
	public void removeOTN(final ObjectTypeNode otn) {
		this.templateToOTN.remove(otn.template);
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
			final ObjectTypeNode otn = this.templateToOTN.get(path.getTemplate());
			if (otn != null)
				otn.shareNode(path);
			else
				this.putOTN(new ObjectTypeNode(network, path));
		}
	}
}
