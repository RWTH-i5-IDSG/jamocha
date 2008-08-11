/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.workingmemory;

import java.io.Serializable;

import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> a (alpha- or beta-) working
 *         memory element.
 */
public interface WorkingMemoryElement extends Serializable {

	/**
	 * returns the complete description string with all its content
	 */
	public String toString();

	/**
	 * returns the wme as fact tuple (maybe with length 1 for alpha wme)
	 */
	public FactTuple getFactTuple();

	/**
	 * returns the first fact (for alpha wme, this is the fact itself)
	 */
	public Fact getFirstFact();

	/**
	 * returns the last fact (for alpha wme, this is the fact itself)
	 */
	public Fact getLastFact();

	/**
	 * determines, whether this is a standalone (alpha-)fact or a beta-fact.
	 */
	public boolean isStandaloneFact();

}
