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

package org.jamocha.engine.workingmemory.elements;

import java.io.Serializable;
import java.util.Iterator;

import org.jamocha.engine.Dumpable;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.formatter.Formattable;

/**
 * @author Peter Lin
 * 
 * Template defines the methods to access an object, which is the equivalent of
 * un-ordered facts. It defines all the necessary methods for Deftemplate.
 */
public interface Template extends Serializable, Dumpable, Formattable {
	/**
	 * The name of the template may be the fully qualified class name, or an
	 * alias.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * templates may have 1 or more slots. A slot is a named column with a
	 * specific type of value.
	 * 
	 * @return
	 */
	int getNumberOfSlots();

	/**
	 * Return an array of all the slots.
	 * 
	 * @return
	 */
	TemplateSlot[] getAllSlots();

	/**
	 * Return the slot with the String name
	 * 
	 * @return
	 */
	TemplateSlot getSlot(String name);

	/**
	 * Get the Slot at the given column id
	 * 
	 * @param column
	 * @return
	 */
	TemplateSlot getSlot(int column);

	/**
	 * if watch is set to true, the rule engine will pass events when the fact
	 * traverses the network.
	 * 
	 * @return
	 */
	boolean getWatch();

	/**
	 * Set the watch flag
	 * 
	 * @param watch
	 */
	void setWatch(boolean watch);

	/**
	 * If a template has a parent, the method should return the parent,
	 * otherwise it should return null
	 * 
	 * @return
	 */
	Template getParent();

	/**
	 * set the parent template
	 * 
	 * @param parent
	 */
	void setParent(Template parent);

	/**
	 * @return
	 */
	String getClassName();

	/**
	 * If the template is currently in use, we should not remove it until all
	 * the dependent rules are removed first.
	 * 
	 * @return
	 */
	boolean inUse();

	/**
	 * 
	 * @return
	 */
	String toString();

	/**
	 * 
	 * @return
	 */
	String toPPString();

	public Iterator<Tag> getTags();

	public Iterator<Tag> getTags(Class<? extends Tag> tagClass);

	public void addTag(Tag t);
}
