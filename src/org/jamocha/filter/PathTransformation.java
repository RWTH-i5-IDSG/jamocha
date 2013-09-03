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
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
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
		 * set of other paths the path corresponding to this PathInfo has been
		 * joined with
		 */
		Set<Path> joinedWith;
	}

	/**
	 * Maps paths to their addresses, the corresponding node of the addresses,
	 * and the set of other paths they have been joined with.
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

	public static Node getCurrentlyLowestNode(final Path path) {
		return getPathInfo(path).currentlyLowestNode;
	}

	public static FactAddress getFactAddressInCurrentlyLowestNode(
			final Path path) {
		return getPathInfo(path).factAddressInCurrentlyLowestNode;
	}

	public static Set<Path> getJoinedWith(final Path path) {
		return getPathInfo(path).joinedWith;
	}

	public static void setCurrentlyLowestNode(final Path path,
			final Node currentlyLowestNode) {
		getPathInfo(path).setCurrentlyLowestNode(currentlyLowestNode);
	}

	public static void setFactAddressInCurrentlyLowestNode(final Path path,
			final FactAddress factAddressInCurrentlyLowestNode) {
		getPathInfo(path).setFactAddressInCurrentlyLowestNode(
				factAddressInCurrentlyLowestNode);
	}

	public static void setJoinedWith(final Path path, final Set<Path> joinedWith) {
		getPathInfo(path).setJoinedWith(joinedWith);
	}

	public static void setJoinedWith(final Path... joined) {
		assert 0 < joined.length;
		final Path path = joined[0];
		final Set<Path> newPaths = new HashSet<>();
		for (final Path newPath : joined) {
			newPaths.add(newPath);
		}
		getPathInfo(path).setJoinedWith(newPaths);
	}

	public static void setPathInfo(final Path path, final PathInfo pathInfo) {
		if (pathInfo != null)
			addressMapping.put(path, pathInfo);
	}

}
