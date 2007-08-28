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

import java.util.ArrayList;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * Deffact is a concrete implementation of Fact interface. It is equivalent to
 * deffact in CLIPS.
 */
public class Deffact implements Fact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Template template = null;

	protected Object objInstance;

	protected Slot[] slots = null;

	protected Slot[] boundSlots = null;

	/**
	 * the Fact id must be unique, since we use it for the indexes
	 */
	protected long id = -1;

	private long ticket;

	private long timeStamp;

	protected boolean hasBinding = false;

	private EqualityIndex Eindex = null;

	/**
	 * this is the default constructor
	 * 
	 * @param instance
	 * @param values
	 */
	public Deffact(Template template, Object instance, Slot[] values) {
		this.template = template;
		this.objInstance = instance;
		this.slots = values;
		this.timeStamp = System.nanoTime();
		this.ticket = Rete.drawTicket();
	}

	/**
	 * 
	 * @param util
	 */
	public void compileBinding(Rule util) {
		ArrayList<Slot> list = new ArrayList<Slot>();
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].value.getType().equals(JamochaType.BINDING)) {
				this.hasBinding = true;
				list.add(this.slots[idx]);
				BoundParam bp = (BoundParam) this.slots[idx].value.getObjectValue();
				Binding bd = util.getBinding(bp.getVariableName());
				if (bd != null) {
					bp.rowId = bd.getLeftRow();
					bp.column = bd.getLeftIndex();
				}
			}
		}
		if (list.size() > 0) {
			this.boundSlots = (Slot[]) list.toArray(new Slot[list.size()]);
		}
	}

	/**
	 * In some cases, a deffact may have bindings. This is a design choice. When
	 * rules are parsed and compiled, actions that assert facts are converted to
	 * Deffact instances with BoundParam for the slot value.
	 * 
	 * @return
	 */
	public boolean hasBinding() {
		return this.hasBinding;
	}

	public void resolveValues(Rete engine, Fact[] triggerFacts) {
		for (int idx = 0; idx < this.boundSlots.length; idx++) {
			if (this.boundSlots[idx].getValue().getType() == JamochaType.LIST) {
				JamochaValue mvals = this.boundSlots[idx].getValue();
				for (int mdx = 0; mdx < mvals.getListCount(); mdx++) {
					JamochaValue jv = mvals.getListValue(mdx);
					BoundParam bp = (BoundParam) jv.getObjectValue();
					bp.setResolvedValue(engine.getBinding(bp.getVariableName()));
				}
			} else if (this.boundSlots[idx].value.getType().equals(JamochaType.BINDING)) {
				BoundParam bp = (BoundParam) this.boundSlots[idx].value.getObjectValue();
				if (bp.column > -1) {
					bp.setFact(triggerFacts);
				} else {
					bp.setResolvedValue(engine.getBinding(bp.getVariableName()));
				}
			}
		}
	}

	/**
	 * Method returns the value of the given slot at the id.
	 * 
	 * @param id
	 * @return
	 * @throws EvaluationException
	 */
	public JamochaValue getSlotValue(int id) throws EvaluationException {
		try {
			return this.slots[id].value;
		} catch (ArrayIndexOutOfBoundsException e) {
			String templName = "";
			if (this.template != null)
				templName = this.template.getName();
			throw new EvaluationException("Error in getSlotValue, Template: " + this.getTemplate().getName() + " Index does not exist: " + id, e);
		}

	}

	/**
	 * Method returns the value of the given slotname.
	 * 
	 * @param SlotName
	 * @return
	 * @throws EvaluationException
	 */
	public JamochaValue getSlotValue(String name) throws EvaluationException {
		int col = getSlotId(name);
		if (col != -1) {
			return getSlotValue(col);
		} else {
			return null;
		}
	}

	/**
	 * Method will iterate over the slots until finds the match. If no match is
	 * found, it return -1.
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
	 * If the fact is a shadow fact, it will return the object instance. If the
	 * fact is just a deffact and isn't a shadow fact, it return null.
	 * 
	 * @return
	 */
	public Object getObjectInstance() {
		return this.objInstance;
	}

	/**
	 * Method will return the fact in a string format.
	 * 
	 * @return
	 */
	public String toFactString() {
		return format(ParserFactory.getFormatter(true));
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.template.getName());
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

	public String toString() {
		return toPPString();
	}

	/**
	 * Returns the string format for the fact without the fact-id. this is used
	 * to make sure that if an user asserts an equivalent fact, we can easily
	 * check it.
	 * 
	 * @return
	 */
	public EqualityIndex equalityIndex() {
		if (this.Eindex == null) {
			this.Eindex = new EqualityIndex(this);
		}
		return this.Eindex;
	}

	/**
	 * this is used by the EqualityIndex class
	 * 
	 * @return
	 */
	public int hashCode() {
		int hash = 0;
		for (int idx = 0; idx < this.slots.length; idx++) {
			hash += this.slots[idx].getName().hashCode() + this.slots[idx].value.hashCode();
		}
		return hash;
	}

	/**
	 * Return the long factId
	 */
	public long getFactId() {
		return this.id;
	}

	/**
	 * if the factId is -1, the fact will get will the next fact id from Rete
	 * and set it. Otherwise, the fact will use the same one.
	 * 
	 * @param id
	 */
	public void setFactId(long id) {
		this.id = id;
	}

	/**
	 * this is used to reset the id, in the event an user tries to assert the
	 * same fact again, we reset the id to the existing one.
	 * 
	 * @param fact
	 */
	protected void resetID(Fact fact) {
		this.id = fact.getFactId();
	}

	/**
	 * update the slots
	 */
	public void updateSlots(Rete engine, Slot[] updates) {
		for (int idx = 0; idx < updates.length; idx++) {
			Slot uslot = updates[idx];
			if (uslot.value.getType().equals(JamochaType.BINDING)) {
				BoundParam bp = (BoundParam) uslot.value.getObjectValue();
				JamochaValue val = engine.getBinding(bp.getVariableName());
				this.slots[uslot.getId()].value = val;
			} else {
				this.slots[uslot.getId()].value = uslot.value;
			}
		}
	}

	public void updateSlots(Rete engine, SlotConfiguration[] slotConfigs) throws EvaluationException {
		SlotConfiguration slotConfig = null;
		JamochaValue newValue = null;
		for (int idx = 0; idx < slotConfigs.length; idx++) {
			slotConfig = slotConfigs[idx];
			newValue = slotConfig.getValue(engine);
			slots[getSlotId(slotConfig.getSlotName())].value = newValue;
		}
	}

	/**
	 * Return the deftemplate for the fact
	 */
	public Template getTemplate() {
		return this.template;
	}

	/**
	 * Returns the unique ticket of this fact. This is used to sort the
	 * activations by their "time" of arrival.
	 * 
	 * @return the facts ticket.
	 */
	public final long getTicket() {
		return ticket;
	}

	/**
	 * the implementation returns nano time
	 */
	public final long timeStamp() {
		return this.timeStamp;
	}

	/**
	 * the current implementation only compares the values, since the slot names
	 * are equal. It would be a waste of time to compare the slot names. The
	 * exception to the case is when a deftemplate is changed. Since that
	 * feature isn't supported yet, it's currently not an issue. Even if
	 * updating deftemplates is added in the future, the deffacts need to be
	 * updated. If the deffacts weren't updated, it could lead to
	 * NullPointerExceptions.
	 * 
	 * @param fact
	 * @return
	 */
	public boolean equals(Object object) {
		if (object instanceof Fact) {
			Fact fact = (Fact) object;
			boolean eq = true;

			try {
				for (int idx = 0; idx < this.slots.length; idx++) {

					if (!this.slots[idx].value.equals(fact.getSlotValue(idx))) {
						eq = false;
						break;
					}
				}
			} catch (EvaluationException e) {
				// should not occur
				e.printStackTrace();

			}
			return eq;
		}
		return false;
	}

	/**
	 * Convienance method for cloning a fact. If a slot's value is a BounParam,
	 * the cloned fact uses the value of the BoundParam.
	 * 
	 * @return
	 */
	public Deffact cloneFact(Rete engine) {
		Deffact newfact = new Deffact(this.template, this.objInstance, cloneAllSlots());
		Slot[] slts = newfact.slots;
		for (int idx = 0; idx < slts.length; idx++) {
			// probably need to revisit this and make sure
			if (this.slots[idx].getValue().getType() == JamochaType.LIST) {
				JamochaValue mval = this.slots[idx].getValue();
				// check the list to see if there's any bindings
				JamochaValue[] rvals = new JamochaValue[mval.getListCount()];
				for (int mdx = 0; mdx < mval.getListCount(); mdx++) {
					JamochaValue v2 = mval.getListValue(mdx);
					if (v2.getType().equals(JamochaType.BINDING)) {
						try {
							rvals[mdx] = JamochaValue.newObject(((BoundParam) v2.getObjectValue()).getValue(engine));
						} catch (EvaluationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						rvals[mdx] = v2;
					}
				}
				slts[idx].value = JamochaValue.newList(rvals);
			} else if (this.slots[idx].value.getType().equals(JamochaType.BINDING)) {
				try {
					slts[idx].value = ((BoundParam) this.slots[idx].value.getObjectValue()).getValue(engine);
				} catch (EvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				slts[idx].value = this.slots[idx].value;
			}
		}
		return newfact;
	}

	/**
	 * clone the slots
	 * 
	 * @return
	 */
	private Slot[] cloneAllSlots() {
		Slot[] cloned = new Slot[this.slots.length];
		for (int idx = 0; idx < cloned.length; idx++) {
			cloned[idx] = (Slot) this.slots[idx].clone();
		}
		return cloned;
	}

	/**
	 * this will make sure the fact is GC immediately
	 */
	public void clear() {
		this.template = null;
		this.objInstance = null;
		this.slots = null;
		this.id = 0;
		this.timeStamp = 0;
	}

	public boolean getSlotSilence(int idx) {
		return this.slots[idx].silent;
	}

	public boolean getSlotSilence(String slotName) {
		return this.slots[getSlotId(slotName)].silent;
	}

	public String getDump(String modName) {
		StringBuffer buf = new StringBuffer();
		buf.append("(assert (" + modName + "::" + this.template.getName());
		if (this.slots.length > 0) {
			buf.append(" ");
		}
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("(" + this.slots[idx].getName() + " " + ConversionUtils.formatSlot(this.slots[idx].value) + ") ");
		}
		buf.append(") )");
		return buf.toString();
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
