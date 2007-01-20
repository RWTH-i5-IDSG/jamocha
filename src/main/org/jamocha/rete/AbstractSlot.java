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

import java.io.Serializable;

/**
 * @author Peter Lin
 *
 * AbstractSlot contains common attributes of Slot, multislot and
 * binding. Slot classes need to implement the clone method for
 * cloning the slots. This is necessary because slots are used to
 * parse CLIPS and for the RETE nodes.
 * <br>
 * 
 */
public abstract class AbstractSlot implements Serializable, Cloneable {

	/**
	 * The name of the slot
	 */
	private String name;

	/**
	 * the id of the slot
	 */
	private int id;

	/**
	 * The type of the value
	 */
	private int type = -1;

	/**
	 * depth is a place holder for ordered facts, which is a list
	 * of symbols. For the first version, ordered facts are not
	 * implemented. it is also used in the case a condition has
	 * multiple equal/not equal as in (attr2 "me" | "you" | ~"her" | ~"she")
	 */
	private int depth;

	/**
	 * in some cases, users may want a template to have a default value
	 */
	private boolean hasDefault = false;

	/**
	 * node count is used to keep track of how many nodes use the given
	 * slot. This is done for statistical purposes, which serve 3 main
	 * functions.
	 * 1. provide a way to calculate the relative importance of a slot
	 * with regard to the entire RETE network
	 * 2. provide a way to optimize runtime execution
	 * 3. provide valuable information for engine management
	 */
	private int nodeCount = 1;

	/**
	 * Get the name of the slot
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the slot
	 * @param text
	 */
	public void setName(String text) {
		this.name = text;
	}

	public int getValueType() {
		return this.type;
	}

	public void setValueType(int type) {
		this.type = type;
	}

	/**
	 * the id is the column id, this is the sequence java
	 * introspection returns the fields for the object
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Set the column id for the slot. the id is the position
	 * of the slot in the deftemplate
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * return the number of nodes the given slot participates
	 * in. It may not be a complete count. In some cases, it
	 * may only count the direct successors of ObjectTypeNode
	 * @return
	 */
	public int getNodeCount() {
		return this.nodeCount;
	}

	/**
	 * Increment the node count
	 */
	public void incrementNodeCount() {
		this.nodeCount++;
	}

	/**
	 * decrement the node count
	 *
	 */
	public void decrementNodeCount() {
		--this.nodeCount;
	}

	/**
	 * A convienance method to clone slots. subclasses must implement
	 * this method.
	 */
	public abstract Object clone();
}