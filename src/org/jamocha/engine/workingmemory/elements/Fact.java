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

package org.jamocha.engine.workingmemory.elements;

import java.util.Iterator;

import org.jamocha.engine.Dumpable;
import org.jamocha.engine.Engine;
import org.jamocha.engine.EqualityIndex;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.formatter.Formattable;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
 * 
 * Base interface for Facts
 */
public interface Fact extends WorkingMemoryElement, Formattable, Dumpable {

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
	 * Return the unique ID for the fact
	 * 
	 * @return
	 */
	long getFactId();

	/**
	 * update the slots
	 */
	void updateSlots(Engine engine, Slot[] slots);

	void updateSlots(Engine engine, SlotConfiguration[] slots)
			throws EvaluationException;

	/**
	 * Return the Deftemplate for the fact
	 * 
	 * @return
	 */
	Template getTemplate();

	/**
	 * finalize the object and make it ready for GC
	 */
	void clear();

	/**
	 * the timestamp for the fact
	 * 
	 * @return
	 */
	long getCreationTimeStamp();

	/**
	 * 
	 */
	EqualityIndex equalityIndex();

	void setFactId(long id);

	public boolean isSlotSilent(int idx);

	public boolean isSlotSilent(String slotName);

	public Iterator<Tag> getTags();

	public Iterator<Tag> getTags(Class<Tag> tagClass);

	public void addTag(Tag t);

}