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
import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryFactToFactIdentifier;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;

/**
 * Root node implementation (not part of the {@link Node} type hierarchy).
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class RootNode implements MemoryFactToFactIdentifier {

	/**
	 * Maps from {@link Template} to corresponding {@link ObjectTypeNode}
	 */
	private final Map<Template, ObjectTypeNode> templateToOTN = new HashMap<>();

	private final Map<FactIdentifier, MemoryFact> facts = new HashMap<>();
	// store weak MemoryFact references mapping them to their FactIdentifiers
	private final Map<MemoryFact, FactIdentifier> factIdentifiers = new WeakHashMap<>();
	private int factIdentifierCounter = 0;

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} calling {@link ObjectTypeNode#assertFact(Fact)}.
	 * 
	 * @param fact
	 *            {@link Fact} to be asserted
	 */
	public FactIdentifier[] assertFacts(final Fact... facts) {
		final Map<Fact, MemoryFact> factToMemoryFact = new HashMap<>();
		Arrays.stream(facts).collect(groupingBy(Fact::getTemplate)).forEach((t, factlist) -> {
			final Fact[] fs = toArray(factlist, Fact[]::new);
			final MemoryFact[] mfs = this.templateToOTN.get(t).assertFact(fs);
			IntStream.range(0, mfs.length).forEach(i -> factToMemoryFact.put(fs[i], mfs[i]));
		});
		final int length = facts.length;
		final FactIdentifier[] factIdentifiers = new FactIdentifier[length];
		for (int i = 0; i < length; ++i) {
			final MemoryFact memoryFact = factToMemoryFact.get(facts[i]);
			if (null == memoryFact) {
				continue;
			}
			final int id = factIdentifierCounter++;
			final FactIdentifier factIdentifier = new FactIdentifier(id);
			factIdentifiers[i] = factIdentifier;
			this.facts.put(factIdentifier, memoryFact);
			this.factIdentifiers.put(memoryFact, factIdentifier);
		}
		return factIdentifiers;
	}

	/**
	 * Passes the {@link Fact} given to the {@link ObjectTypeNode} corresponding to its
	 * {@link Template} calling {@link ObjectTypeNode#retractFact(Fact)}.
	 * 
	 * @param fact
	 *            {@link Fact} to be retracted
	 */
	public void retractFacts(final FactIdentifier... factIdentifiers) {
		Arrays.stream(factIdentifiers)
				.map(facts::remove)
				.filter(Objects::nonNull)
				.collect(groupingBy(f -> f.getTemplate()))
				.forEach(
						(t, f) -> this.templateToOTN.get(t).retractFact(
								toArray(f, MemoryFact[]::new)));
	}

	/**
	 * Deletes all facts in the system and resets the fact index counter
	 */
	public void reset() {
		retractFacts(toArray(getMemoryFacts().keySet(), FactIdentifier[]::new));
		this.factIdentifierCounter = 0;
	}

	public MemoryFact getMemoryFact(final FactIdentifier factIdentifier) {
		return this.facts.get(factIdentifier);
	}

	@Override
	public FactIdentifier getFactIdentifier(final MemoryFact memoryFact) {
		return this.factIdentifiers.get(memoryFact);
	}

	public Map<FactIdentifier, MemoryFact> getMemoryFacts() {
		return Collections.unmodifiableMap(this.facts);
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
	 * Returns a collection of all child OTNs.
	 * 
	 * @return a collection of all child OTNs
	 */
	public Collection<ObjectTypeNode> getOTNs() {
		return this.templateToOTN.values();
	}

	/**
	 * Removes all OTNs, deletes all facts in the root node, and resets the fact identifier counter
	 * to zero.
	 */
	public void clear() {
		this.templateToOTN.clear();
		this.facts.clear();
		this.factIdentifiers.clear();
		this.factIdentifierCounter = 0;
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
			this.templateToOTN.computeIfAbsent(path.getTemplate(),
					t -> new ObjectTypeNode(network, t)).shareNode(Collections.emptyMap(), path);
		}
	}
}
