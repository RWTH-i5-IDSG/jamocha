/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.logging;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public enum MarkerType {
	FACTS(MarkerManager.getMarker("FACTS")), RULES(MarkerManager.getMarker("RULES")), ACTIVATIONS(MarkerManager
			.getMarker("ACTIVATIONS"));
	final Marker commonMarker;

	public Marker createChild(final String name) {
		return MarkerManager.getMarker(commonMarker.getName() + ":" + name).setParents(commonMarker);
	}
}
