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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.tuple.Pair;
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
@Log4j2
public class RootNode {

	/**
	 * Maps from {@link Template} to corresponding {@link ObjectTypeNode}
	 */
	private final Map<Template, ObjectTypeNode> templateToOTN = new HashMap<>();

	private final Map<FactIdentifier, MemoryFact> facts = new HashMap<>();
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
				System.out.println("FALSE");
				continue;
			}
			final int id = factIdentifierCounter++;
			final FactIdentifier factIdentifier = new FactIdentifier(id);
			factIdentifiers[i] = factIdentifier;
			this.facts.put(factIdentifier, memoryFact);
			log.info(memoryFact.getTemplate().getInstanceMarker(), "==> f-{}\t{}", id, memoryFact);
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
	private void retractFacts(final List<Pair<Integer, MemoryFact>> facts) {
		final Map<Boolean, List<Pair<Integer, MemoryFact>>> partition =
				facts.stream().collect(Collectors.partitioningBy(p -> null == p.getRight()));
		partition.get(true).forEach(p -> log.error("Unable to find fact f-{}", p.getLeft()));
		partition
				.get(false)
				.stream()
				.collect(groupingBy(f -> f.getRight().getTemplate()))
				.forEach(
						(t, f) -> {
							this.templateToOTN.get(t).retractFact(
									toArray(f.stream().map(Pair::getRight), MemoryFact[]::new));
							for (final Pair<Integer, MemoryFact> p : f) {
								log.info(p.getRight().getTemplate().getInstanceMarker(),
										"<== f-{}\t{}", p.getLeft(), p.getRight());
							}
						});
	}

	public void retractFacts(final FactIdentifier... factIdentifiers) {
		retractFacts(Arrays.stream(factIdentifiers).map(i -> Pair.of(i.getId(), facts.remove(i)))
				.collect(toList()));
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
