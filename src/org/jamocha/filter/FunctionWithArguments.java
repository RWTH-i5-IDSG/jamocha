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

import java.util.Set;

import org.jamocha.engine.nodes.Node;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public interface FunctionWithArguments extends Function {

	public void gatherPaths(final Set<Path> paths);

	/**
	 * 
	 * @param translation
	 * @param childNode
	 * @return
	 */
	public FunctionWithArguments translatePath(
			final PathTransformation translation, final Node childNode);

}
