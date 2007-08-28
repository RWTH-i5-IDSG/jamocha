/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import org.jamocha.formatter.Formattable;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.nodes.Assertable;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * Base interface for Facts
 */
public interface Fact extends Assertable, Dumpable, Formattable {

	/**
	 * Return the value at the given slot id
	 * 
	 * @param id
	 * @return
	 * @throws EvaluationException 
	 */
	JamochaValue getSlotValue(int id) throws EvaluationException;

	/**
	 * Return the value at the given slot id
	 * 
	 * @param id
	 * @return
	 * @throws EvaluationException 
	 */
	JamochaValue getSlotValue(String name) throws EvaluationException;

	/**
	 * Return id of the given slot name
	 * 
	 * @param name
	 * @return
	 */
	int getSlotId(String name);

	/**
	 * Return the object instance linked to the fact
	 * 
	 * @return
	 */
	Object getObjectInstance();

	/**
	 * Method will return the fact in a string format.
	 * 
	 * @return
	 */
	String toFactString();

	/**
	 * Return the unique ID for the fact
	 * 
	 * @return
	 */
	long getFactId();

	/**
	 * If we need to update slots
	 * 
	 * @param slots
	 */
	void updateSlots(Rete engine, Slot[] slots);

	void updateSlots(Rete engine, SlotConfiguration[] slots)
			throws EvaluationException;

	/**
	 * Return the Deftemplate for the fact
	 * 
	 * @return
	 */
	Template getTemplate();

	void compileBinding(Rule util);

	/**
	 * finalize the object and make it ready for GC
	 */
	void clear();

	/**
	 * the timestamp for the fact
	 * 
	 * @return
	 */
	long timeStamp();

	/**
	 * 
	 */
	EqualityIndex equalityIndex();

	void setFactId(long id);

	public boolean getSlotSilence(int idx);

	public boolean getSlotSilence(String slotName);

}
