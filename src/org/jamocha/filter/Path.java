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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.Node;

/**
 * A Path describes an element of a condition part of a rule. It "traces" its position in the
 * network during construction time.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class Path {

	/**
	 * {@link Template} of the path
	 * 
	 * @return {@link Template template} of the path
	 */
	@Getter
	final Template template;

	/**
	 * {@link Node}, the path is currently produced by
	 * 
	 * @param currentlyLowestNode
	 *            {@link Node node}, the path is currently produced by
	 * @return {@link Node node}, the path is currently produced by
	 */
	@Getter
	@Setter
	private Node currentlyLowestNode;

	/**
	 * {@link FactAddress} identifying the fact in the current {@link Node node}
	 * 
	 * @param factAddressInCurrentlyLowestNode
	 *            {@link FactAddress fact address} identifying the fact in the current {@link Node
	 *            node}
	 * @return {@link FactAddress fact address} identifying the fact in the current {@link Node
	 *         node}
	 */
	@Getter
	@Setter
	private FactAddress factAddressInCurrentlyLowestNode;

	/**
	 * Set of paths this path has been joined with (including itself)
	 * 
	 * @param joinedWith
	 *            set of paths this path has been joined with (including itself)
	 * @return set of paths this path has been joined with (including itself)
	 */
	@Getter
	@Setter
	private Set<Path> joinedWith;

	private Optional<Backup> backup;

	@Value
	private class Backup {
		Node currentlyLowestNode;
		FactAddress factAddressInCurrentlyLowestNode;
		Set<Path> joinedWith;

		Backup() {
			this.currentlyLowestNode = Path.this.currentlyLowestNode;
			this.factAddressInCurrentlyLowestNode = Path.this.factAddressInCurrentlyLowestNode;
			this.joinedWith = Path.this.joinedWith;
		}
	}

	/**
	 * Extracts the {@link SlotType} of the Slot corresponding to {@link SlotAddress addr}.
	 * 
	 * @param addr
	 *            {@link SlotAddress slot address} of the Slot one is interested in
	 * @return the {@link SlotType} of the Slot corresponding to the parameter given
	 */
	public SlotType getTemplateSlotType(final SlotAddress addr) {
		return addr.getSlotType(this.template);
	}

	/**
	 * For all {@link Path paths} passed, this method sets their sets of joined {@link Path paths}
	 * to contain all {@link Path paths} passed.
	 * 
	 * @param joined
	 *            list of {@link Path paths} to set their set of joined {@link Path paths} to
	 *            contain all {@link Path paths} passed
	 */
	public static void setJoinedWithForAll(final Path... joined) {
		final Set<Path> paths = new HashSet<>();
		for (final Path path : joined) {
			paths.add(path);
		}
		for (final Path path : joined) {
			path.setJoinedWith(paths);
		}
	}

	public Path(final Template template, final Node currentlyLowestNode,
			final FactAddress factAddressInCurrentlyLowestNode, final Path... joinedWith) {
		super();
		this.template = template;
		this.currentlyLowestNode = currentlyLowestNode;
		this.factAddressInCurrentlyLowestNode = factAddressInCurrentlyLowestNode;
		this.joinedWith = new HashSet<>(joinedWith.length);
		this.joinedWith.add(this);
		for (final Path path : joinedWith) {
			this.joinedWith.add(path);
		}
	}

	public Path(final Template template, final Path... joinedWith) {
		this(template, null, null, joinedWith);
	}

	@Override
	public String toString() {
		return "Path";
	}

	public void cachedOverride(final Node currentlyLowestNode,
			final FactAddress factAddressInCurrentlyLowestNode, final Set<Path> joinedWith) {
		assert !this.backup.isPresent();
		this.backup = Optional.of(new Backup());
		this.currentlyLowestNode = currentlyLowestNode;
		this.factAddressInCurrentlyLowestNode = factAddressInCurrentlyLowestNode;
		this.joinedWith = joinedWith;
	}

	public void restoreCache() {
		assert this.backup.isPresent();
		final Backup backupInstance = this.backup.get();
		this.currentlyLowestNode = backupInstance.currentlyLowestNode;
		this.factAddressInCurrentlyLowestNode = backupInstance.factAddressInCurrentlyLowestNode;
		this.joinedWith = backupInstance.joinedWith;
		this.backup = Optional.empty();
	}
}
