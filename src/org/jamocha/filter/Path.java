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
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Path {

	/**
	 * {@link Template} of the path
	 * 
	 * @return {@link Template template} of the path
	 */
	final Template template;

	/**
	 * {@link Node}, the path is currently produced by
	 * 
	 * @param currentlyLowestNode
	 *            {@link Node node}, the path is currently produced by
	 * @return {@link Node node}, the path is currently produced by
	 */
	Node currentlyLowestNode;

	/**
	 * {@link FactAddress} identifying the fact in the current {@link Node node}
	 * 
	 * @param factAddressInCurrentlyLowestNode
	 *            {@link FactAddress fact address} identifying the fact in the current {@link Node
	 *            node}
	 * @return {@link FactAddress fact address} identifying the fact in the current {@link Node
	 *         node}
	 */
	FactAddress factAddressInCurrentlyLowestNode;

	/**
	 * Set of paths this path has been joined with (including itself)
	 * 
	 * @param joinedWith
	 *            set of paths this path has been joined with (including itself)
	 * @return set of paths this path has been joined with (including itself)
	 */
	Set<Path> joinedWith;

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

}
