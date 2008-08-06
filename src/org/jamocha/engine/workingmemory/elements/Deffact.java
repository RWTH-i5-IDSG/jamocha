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

import java.util.HashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.ConstraintViolationException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.EqualityIndex;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.nodes.FactTupleImpl;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.engine.workingmemory.elements.tags.TagIterator;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

/**
 * @author Peter Lin
 * @author Josef Hahn
 * 
 * Deffact is a concrete implementation of Fact interface. It is equivalent to
 * deffact in CLIPS.
 */
public class Deffact implements Fact {

	/**
	 * 
	 */

	protected List<Tag> tags;

	private static final long serialVersionUID = 1L;

	protected Template template = null;

	protected Slot[] slots = null;

	protected Slot[] boundSlots = null;

	/**
	 * the Fact id must be unique, since we use it for the indexes
	 */
	protected long id = -1;

	private long timeStamp;

	protected boolean hasBinding = false;

	private EqualityIndex Eindex = null;

	/**
	 * this is the default constructor
	 * 
	 * @param instance
	 * @param values
	 */
	public Deffact(final Template template, final Slot[] values) {
		this.template = template;
		slots = values;
		timeStamp = System.nanoTime();
		tags = new ArrayList<Tag>();
	}
	
	
	/**
	 * this constructor should only be used for debugging purposes
	 * and for writing test-cases. this is not the usual way
	 * of creating facts in jamocha!
	 * 
	 * This string format is "templatename;att1=val1;att2=val2;..."
	 * @param factdef
	 */
	public Deffact(String factdef, Engine e) {
		
		String[] split = factdef.split(";");
		
		String templateName = split[0];
		
		Map<String,String> slots = new HashMap<String, String>();
		for (int i=1; i< split.length; i++) {
			String field = split[i];
			String[] foo = field.split("=");
			String name = foo[0];
			String value = foo[1];
			slots.put(name, value);
		}
		
		TemplateSlot[] tslotArr = new TemplateSlot[slots.keySet().size()];
		Slot[] slotArr = new Slot[slots.keySet().size()];
		int i = 0;
		
		for (String key : slots.keySet()) {
			tslotArr[i++] = new TemplateSlot(key);
		}

		Deftemplate t = new Deftemplate(templateName, templateName, tslotArr);

		try {
			e.addTemplate(t);
		} catch (EvaluationException e1) {
			Logging.logger(this.getClass()).fatal(e1);
		}

		try {
			i = 0;
			for (String key : slots.keySet()) {
				slotArr[i] = tslotArr[i].createSlot(e);

				slotArr[i].setValue(JamochaValue.newString(slots.get(key)));
				i++;
			}		
		} catch (ConstraintViolationException e1) {
			Logging.logger(this.getClass()).fatal(e1);
		} catch (EvaluationException e2) {
			Logging.logger(this.getClass()).fatal(e2);
		}

		this.template = t;
		this.slots = slotArr;
		timeStamp = System.nanoTime();
		tags = new ArrayList<Tag>();

		
		
		
	}
	

	/**
	 * 
	 * @param util
	 */
	// TODO remove public void compileBinding(Rule util) {
	// ArrayList<Slot> list = new ArrayList<Slot>();
	// for (int idx = 0; idx < this.slots.length; idx++) {
	// if (this.slots[idx].value.getType().equals(JamochaType.BINDING)) {
	// this.hasBinding = true;
	// list.add(this.slots[idx]);
	// BoundParam bp = (BoundParam) this.slots[idx].value.getObjectValue();
	// Binding bd = util.getBinding(bp.getVariableName());
	// if (bd != null) {
	// bp.setRow( bd.getLeftRow() );
	// bp.setColumn( bd.getLeftIndex() );
	// }
	// }
	// }
	// if (list.size() > 0) {
	// this.boundSlots = (Slot[]) list.toArray(new Slot[list.size()]);
	// }
	// }
	/**
	 * In some cases, a deffact may have bindings. This is a design choice. When
	 * rules are parsed and compiled, actions that assert facts are converted to
	 * Deffact instances with BoundParam for the slot value.
	 * 
	 * @return
	 */
	public boolean hasBinding() {
		return hasBinding;
	}

	public void resolveValues(final Engine engine, final Fact[] triggerFacts) {
		for (int idx = 0; idx < boundSlots.length; idx++)
			if (boundSlots[idx].getValue().getType() == JamochaType.LIST) {
				final JamochaValue mvals = boundSlots[idx].getValue();
				for (int mdx = 0; mdx < mvals.getListCount(); mdx++) {
					final JamochaValue jv = mvals.getListValue(mdx);
					final BoundParam bp = (BoundParam) jv.getObjectValue();
					bp
							.setResolvedValue(engine.getBinding(bp
									.getVariableName()));
				}
			} else if (boundSlots[idx].value.getType().equals(
					JamochaType.BINDING)) {
				final BoundParam bp = (BoundParam) boundSlots[idx].value
						.getObjectValue();
				if (bp.getColumn() > -1)
					bp.setFact(triggerFacts);
				else
					bp
							.setResolvedValue(engine.getBinding(bp
									.getVariableName()));
			}
	}

	/**
	 * Method returns the value of the given slot at the id.
	 * 
	 * @param id
	 * @return
	 * @throws EvaluationException
	 */
	public JamochaValue getSlotValue(final int id) throws EvaluationException {
		try {
			return slots[id].value;
		} catch (final ArrayIndexOutOfBoundsException e) {
			String templName = null;
			if (template != null)
				templName = template.getName();
			throw new EvaluationException("Error in getSlotValue, Template: "
					+ templName + " Index does not exist: " + id, e);
		}

	}

	/**
	 * Method returns the value of the given slotname.
	 * 
	 * @param SlotName
	 * @return
	 * @throws EvaluationException
	 */
	public JamochaValue getSlotValue(final String name)
			throws EvaluationException {
		final int col = getSlotId(name);
		if (col != -1)
			return getSlotValue(col);
		else
			return null;
	}

	/**
	 * Method will iterate over the slots until finds the match. If no match is
	 * found, it return -1.
	 */
	public int getSlotId(final String name) {
		int col = -1;
		for (int idx = 0; idx < slots.length; idx++)
			if (slots[idx].getName().equals(name)) {
				col = idx;
				break;
			}
		return col;
	}

	/**
	 * Method will return the fact in a string format.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return format(ParserFactory.getFormatter(true));
	}

	/**
	 * Returns the string format for the fact without the fact-id. this is used
	 * to make sure that if an user asserts an equivalent fact, we can easily
	 * check it.
	 * 
	 * @return
	 */
	public EqualityIndex equalityIndex() {
		if (Eindex == null)
			Eindex = new EqualityIndex(this);
		return Eindex;
	}

	/**
	 * this is used by the EqualityIndex class
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for (int idx = 0; idx < slots.length; idx++)
			hash += slots[idx].getName().hashCode()
					+ slots[idx].value.hashCode();
		return hash;
	}

	/**
	 * Return the long factId
	 */
	public long getFactId() {
		return id;
	}

	/**
	 * if the factId is -1, the fact will get will the next fact id from Rete
	 * and set it. Otherwise, the fact will use the same one.
	 * 
	 * @param id
	 */
	public void setFactId(final long id) {
		this.id = id;
	}

	/**
	 * this is used to reset the id, in the event an user tries to assert the
	 * same fact again, we reset the id to the existing one.
	 * 
	 * @param fact
	 */
	protected void resetID(final Fact fact) {
		id = fact.getFactId();
	}

	/**
	 * update the slots
	 */
	public void updateSlots(final Engine engine, final Slot[] updates) {
		for (int idx = 0; idx < updates.length; idx++) {
			final Slot uslot = updates[idx];
			if (uslot.value.getType().equals(JamochaType.BINDING)) {
				final BoundParam bp = (BoundParam) uslot.value.getObjectValue();
				final JamochaValue val = engine
						.getBinding(bp.getVariableName());
				slots[uslot.getId()].value = val;
			} else
				slots[uslot.getId()].value = uslot.value;
		}
	}

	public void updateSlots(final Engine engine,
			final SlotConfiguration[] slotConfigs) throws EvaluationException {
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
		return template;
	}

	/**
	 * the implementation returns nano time
	 */
	public final long getCreationTimeStamp() {
		return timeStamp;
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
	@Override
	public boolean equals(final Object object) {
		if (object instanceof Fact) {
			final Fact fact = (Fact) object;

			if (!fact.getTemplate().equals(getTemplate()))
				return false;

			boolean eq = true;

			try {
				for (int idx = 0; idx < slots.length; idx++)
					if (!slots[idx].value.equals(fact.getSlotValue(idx))) {
						eq = false;
						break;
					}
			} catch (final EvaluationException e) {
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
	public Deffact cloneFact(final Engine engine) {
		final Deffact newfact = new Deffact(template, cloneAllSlots());
		final Slot[] slts = newfact.slots;
		for (int idx = 0; idx < slts.length; idx++)
			// probably need to revisit this and make sure
			if (slots[idx].getValue().getType() == JamochaType.LIST) {
				final JamochaValue mval = slots[idx].getValue();
				// check the list to see if there's any bindings
				final JamochaValue[] rvals = new JamochaValue[mval
						.getListCount()];
				for (int mdx = 0; mdx < mval.getListCount(); mdx++) {
					final JamochaValue v2 = mval.getListValue(mdx);
					if (v2.getType().equals(JamochaType.BINDING))
						try {
							rvals[mdx] = JamochaValue
									.newObject(((BoundParam) v2
											.getObjectValue()).getValue(engine));
						} catch (final EvaluationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else
						rvals[mdx] = v2;
				}
				slts[idx].value = JamochaValue.newList(rvals);
			} else if (slots[idx].value.getType().equals(JamochaType.BINDING))
				try {
					slts[idx].value = ((BoundParam) slots[idx].value
							.getObjectValue()).getValue(engine);
				} catch (final EvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				slts[idx].value = slots[idx].value;
		return newfact;
	}

	/**
	 * clone the slots
	 * 
	 * @return
	 */
	private Slot[] cloneAllSlots() {
		final Slot[] cloned = new Slot[slots.length];
		for (int idx = 0; idx < cloned.length; idx++)
			cloned[idx] = (Slot) slots[idx].clone();
		return cloned;
	}

	/**
	 * this will make sure the fact is GC immediately
	 */
	public void clear() {
		template = null;
		slots = null;
		id = 0;
		timeStamp = 0;
	}

	public boolean isSlotSilent(final int idx) {
		return slots[idx].silent;
	}

	public boolean isSlotSilent(final String slotName) {
		return slots[getSlotId(slotName)].silent;
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	public FactTuple getFactTuple() {
		final FactTuple tuple = new FactTupleImpl(this);
		return tuple;
	}

	public Fact getFirstFact() {
		return this;
	}

	public Fact getLastFact() {
		return this;
	}

	public boolean isStandaloneFact() {
		return true;
	}

	public Iterator<Tag> getTags() {
		return getTags(Tag.class);
	}

	public void addTag(final Tag t) {
		tags.add(t);
	}

	public Iterator<Tag> getTags(final Class<Tag> tagClass) {
		return new TagIterator(tagClass, tags);
	}

	/**
	 * creates text dump for this fact
	 */
	public String getDump() {
		return toString();
	}

}
