/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.nodes;

import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> A sequence of facts.
 */
public interface FactTuple extends WorkingMemoryElement, Iterable<Fact> {

	/**
	 * returns the number of facts in the sequence
	 */
	int length();

	/**
	 * returns the facts themselves as array
	 */
	Fact[] getFacts();

	/**
	 * gets the fact at a given position
	 */
	Fact getFact(int index);

	/**
	 * returns the new FactTuple, which emerges from the old one by appending a
	 * given fact
	 */
	FactTuple appendFact(Fact fact);

	/**
	 * determines, whether 'smallerOne' is a prefix tuple of myself
	 */
	boolean isMySubTuple(FactTuple smallerOne);

	/**
	 * determines, whether 'f' is the last fact of myself
	 */
	boolean isMyLastFact(Fact f);

	/**
	 * gets the maximum creation timestamp from the facts within it
	 * 
	 * @return
	 */
	long getAggregateCreationTimestamp();

}
