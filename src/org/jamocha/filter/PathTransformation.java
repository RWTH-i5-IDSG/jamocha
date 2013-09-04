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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.Node;

/**
 * This class stores all relevant information about {@link Path paths} during construction time. For
 * each {@link Path path}, this class stores the {@link Node node}, the {@link Path path} is
 * currently produced by, the corresponding {@link FactAddress fact address} in there, and all
 * {@link Path paths} joined with the one under investigation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Path
 * @see Node
 * @see FactAddress
 */
public class PathTransformation {
	@Getter
	@Setter
	@AllArgsConstructor
	public static class PathInfo {
		/**
		 * node, the path is currently produced by
		 */
		Node currentlyLowestNode;
		/**
		 * identifies fact in current node
		 */
		FactAddress factAddressInCurrentlyLowestNode;
		/**
		 * set of other paths the path corresponding to this PathInfo has been joined with
		 */
		Set<Path> joinedWith;
	}

	/**
	 * Maps paths to their addresses, the corresponding node of the addresses, and the set of other
	 * paths they have been joined with.
	 */
	private static Map<Path, PathInfo> addressMapping = new HashMap<>();

	private static PathInfo getPathInfo(final Path path) {
		PathInfo pathInfo = addressMapping.get(path);
		if (pathInfo == null) {
			pathInfo = new PathInfo(null, null, new HashSet<Path>());
			addressMapping.put(path, pathInfo);
		}
		return pathInfo;
	}

	/**
	 * Get the {@link Node node}, the given {@link Path path} is currently produced by.
	 * 
	 * @param path
	 *            {@link Path path} to get the {@link Node node} for
	 * @return {@link node} the given {@link Path path} is currently produced by
	 */
	public static Node getCurrentlyLowestNode(final Path path) {
		return getPathInfo(path).currentlyLowestNode;
	}

	/**
	 * Get the {@link FactAddress fact address} in the {@link Node node}, the given {@link Path
	 * path} is currently produced by.
	 * 
	 * @param path
	 *            {@link Path path} to get the {@link FactAddress fact address} for
	 * @return {@link FactAddress fact address} in the {@link Node node}, the given {@link Path
	 *         path} is currently produced by
	 */
	public static FactAddress getFactAddressInCurrentlyLowestNode(final Path path) {
		return getPathInfo(path).factAddressInCurrentlyLowestNode;
	}

	/**
	 * Get the {@link Path paths} the given {@link Path path} is joined with (contains itself).
	 * 
	 * @param path
	 *            {@link Path path} to get the joined {@link Path paths} for
	 * @return {@link Path paths} the given {@link Path path} is joined with (contains itself)
	 */
	public static Set<Path> getJoinedWith(final Path path) {
		return getPathInfo(path).joinedWith;
	}

	/**
	 * Set the {@link Node node}, the given {@link Path path} is currently produced by.
	 * 
	 * @param path
	 *            {@link Path path} to set the {@link Node node} for
	 * @param currentlyLowestNode
	 *            {@link node} the given {@link Path path} is currently produced by
	 */
	public static void setCurrentlyLowestNode(final Path path, final Node currentlyLowestNode) {
		getPathInfo(path).setCurrentlyLowestNode(currentlyLowestNode);
	}

	/**
	 * Set the {@link FactAddress fact address} in the {@link Node node}, the given {@link Path
	 * path} is currently produced by.
	 * 
	 * @param path
	 *            {@link Path path} to set the {@link FactAddress fact address} for
	 * @param factAddressInCurrentlyLowestNode
	 *            {@link FactAddress fact address} in the {@link Node node}, the given {@link Path
	 *            path} is currently produced by
	 */
	public static void setFactAddressInCurrentlyLowestNode(final Path path,
			final FactAddress factAddressInCurrentlyLowestNode) {
		getPathInfo(path).setFactAddressInCurrentlyLowestNode(factAddressInCurrentlyLowestNode);
	}

	/**
	 * Set the {@link Path paths} the given {@link Path path} is joined with (has to contain
	 * itself).
	 * 
	 * @param path
	 *            {@link Path path} to set the joined {@link Path paths} for
	 * @param joinedWith
	 *            {@link Path paths} the given {@link Path path} is joined with (has to contain
	 *            itself)
	 */
	public static void setJoinedWith(final Path path, final Set<Path> joinedWith) {
		getPathInfo(path).setJoinedWith(joinedWith);
	}

	/**
	 * For all {@link Path paths} passed, this method sets their sets of joined {@link Path paths}
	 * to contain all {@link Path paths} passed.
	 * 
	 * @param joined
	 *            list of {@link Path paths} to set their set of joined {@link Path paths} to
	 *            contain all {@link Path paths} passed
	 */
	public static void setJoinedWith(final Path... joined) {
		assert 0 < joined.length;
		final Set<Path> newPaths = new HashSet<>();
		for (final Path path : joined) {
			newPaths.add(path);
		}
		for (final Path path : joined) {
			getPathInfo(path).setJoinedWith(newPaths);
		}
	}

	/**
	 * Shortcut setter for all attributes of {@link PathInfo}. Does nothing, if the given
	 * {@link PathInfo} is {@code null}.
	 * 
	 * @param path
	 *            {@link Path path} to set the {@link PathInfo} for
	 * @param pathInfo
	 *            {@link PathInfo pathInfo} to be set for the given {@link Path path}
	 */
	public static void setPathInfo(final Path path, final PathInfo pathInfo) {
		if (pathInfo != null)
			addressMapping.put(path, pathInfo);
	}

	/**
	 * Clears all {@link Path} mappings.
	 */
	public static void clear() {
		addressMapping.clear();
	}

}
