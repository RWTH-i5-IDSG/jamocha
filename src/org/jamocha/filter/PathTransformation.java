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

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.jamocha.engine.nodes.NetworkFactAddress;
import org.jamocha.engine.nodes.Node;

/**
 * @author Fabian Ohler
 * 
 */
public class PathTransformation {
	// the following sets should be replaced with cheaper containers if possible
	@Getter
	@Setter
	@AllArgsConstructor
	public static class PathInfo {
		/**
		 * node, the path is currently produced by
		 */
		Node currentlyLowestNode;
		/**
		 * set of other paths the path corresponding to this PathInfo has been
		 * joined with
		 */
		Set<Path> joinedWith;
	}

	/**
	 * Set of paths that are currently joined. Initially this contains sets of
	 * one-element-sets (the paths). These sets are then merged step by step.
	 */
	public static Set<Set<Path>> joinedPaths;
	/**
	 * Maps paths to their addresses, the corresponding node of the addresses,
	 * and the set of other paths they have been joined with.
	 */
	public static Map<Path, PathInfo> addressMapping;

	/*
	 * The Node ctor gets a Filter with PathLeafs. It uses the information
	 * stored here to transform the PathLeafs to ParameterLeafs. It will create
	 * NodeInputs as needed.
	 */
}
