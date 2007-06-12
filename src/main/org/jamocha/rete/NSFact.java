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

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * NSFact stands for Non-Shadow Fact. NSFact is different than Deffact which is
 * a shadow fact for an object instance. NSFact should only be used for cases
 * where fact modification isn't needed. In all cases where the application
 * expects to modify facts in the reasoning cycle, Deffacts should be used.
 * Using NSFact for situations where facts are modified or asserted during the
 * reasoning cycle will produce unreliable results. It will violate the
 * principle of truth maintenance, which means the final result is true and
 * accurate.
 * 
 * Cases where NSFact is useful are routing scenarios where the facts are
 * filtered to determien where they should go. In cases like that, the
 * consequence produces results which are used by the application, but aren't
 * used by the rule engine for reasoning.
 */
public class NSFact implements Fact, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Template deftemplate = null;

	private Defclass dclazz = null;

	private Object objInstance;

	private Slot[] slots = null;

	/**
	 * the Fact id must be unique, since we use it for the indexes
	 */
	private long id;

	private long timeStamp = 0;
	
	private EqualityIndex equalityIndex = null;

	/**
	 * 
	 */
	public NSFact(Template template, Defclass clazz, Object instance,
			Slot[] values, long id) {
		this.deftemplate = template;
		this.dclazz = clazz;
		this.objInstance = instance;
		this.slots = values;
		this.id = id;
		this.timeStamp = System.nanoTime();
	}

	/**
	 * The implementation gets the Defclass and passes the objectInstance to
	 * invoke the read method.
	 * 
	 * @see org.jamocha.rete.Fact#getSlotValue(int)
	 */
	public JamochaValue getSlotValue(int id) {
		return dclazz.getSlotValue(id, objInstance);
	}

	/**
	 * Method returns the value of the given slotname.
	 * 
	 * @param SlotName
	 * @return
	 */
	public JamochaValue getSlotValue(String name) {
		int col = getSlotId(name);
		if (col != -1) {
			return getSlotValue(col);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @see org.jamocha.rete.Fact#getSlotId(java.lang.String)
	 */
	public int getSlotId(String name) {
		int col = -1;
		for (int idx = 0; idx < slots.length; idx++) {
			if (slots[idx].getName().equals(name)) {
				col = idx;
				break;
			}
		}
		return col;
	}

	/**
	 * The object instance for the fact
	 */
	public Object getObjectInstance() {
		return this.objInstance;
	}

	/**
	 * The method will return the Fact as a string
	 */
	public String toFactString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.deftemplate.getName() + " ");
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("(" + this.slots[idx].getName() + " "
					+ dclazz.getSlotValue(idx, this.objInstance).toString()
					+ ") ");
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * Return the unique fact id
	 */
	public long getFactId() {
		return this.id;
	}

	/**
	 * Non-Shadow Fact does not implement this, since this method doesn't apply
	 * to facts derived from objects.
	 */
	public void updateSlots(Rete engine, Slot[] updates) {
	}
	public void updateSlots(Rete engine, SlotConfiguration[] slots) {
	}

	/**
	 * Return the deftemplate for the fact
	 */
	public Template getTemplate() {
		return this.deftemplate;
	}

	/**
	 * the implementation returns nano time
	 */
	public long timeStamp() {
		return this.timeStamp;
	}

	/**
	 * clear will set all the references to null. this makes sure objects are
	 * GC.
	 */
	public void clear() {
		this.slots = null;
		this.objInstance = null;
		this.deftemplate = null;
		this.id = 0;
	}

	public void compileBinding(Rule util) {
		// I think, we doesn't need to do anything here since
		// objects can't have bindings as field values
		// TODO check if doing nothing is ok here
	}

	public EqualityIndex equalityIndex() {
		if(equalityIndex == null) {
			equalityIndex = new EqualityIndex(this);
		}
		return equalityIndex;
	}

	public void setFactId(long id) {
		this.id = id;
	}

	public int hashCode() {
		return objInstance.hashCode();
	}

	public boolean equals(Object obj) {
		return objInstance.equals(obj);
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.deftemplate.getName());
		if (this.slots.length > 0) {
			buf.append(" ");
		}
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].value.getType().equals(JamochaType.BINDING)) {
				BoundParam bp = (BoundParam) this.slots[idx].value.getObjectValue();
				buf.append("(" + this.slots[idx].getName() + " ?" + bp.getVariableName() + ") ");
			} else {
				buf.append("(" + this.slots[idx].getName() + " " + ConversionUtils.formatSlot(this.slots[idx].value) + ") ");
			}
		}
		buf.append(")");
		return buf.toString();
	}

	public boolean getSlotSilence(int idx) {
		return slots[idx].silent;
	}
	
	public boolean getSlotSilence(String slotName) {
		return this.slots[getSlotId(slotName)].silent;
	}

}
