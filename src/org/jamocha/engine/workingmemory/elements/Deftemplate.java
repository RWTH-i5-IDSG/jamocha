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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.Constants;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.engine.workingmemory.elements.tags.TagIterator;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

/**
 * @author Peter Lin Deftemplate is equivalent to CLIPS deftemplate<br/>
 * 
 * Deftemplate contains an array of slots that represent un-ordered facts.
 * Currently, deftemplate does not have a reference to the corresponding
 * Defclass, since many objects in java.beans and java.lang.reflect are not
 * serializable. This means when ever we need to lookup the defclass from the
 * deftemplate, we have to use the String form and do the lookup.
 * 
 * Some general design notes about the current implementation. In the case where
 * a class is declared to create the deftemplate, the order of the slots are
 * based on java Introspection. In the case where an user declares the
 * deftemplate from console or directly, the order is the same as the string
 * equivalent. The current implementation does not address redeclaring a
 * deftemplate for a couple of reasons. The primary one is how does it affect
 * the existing RETE nodes. One possible approach is to always add new slots to
 * the end of the deftemplate and ignore the explicit order. Another is to
 * recompute the deftemplate, binds and all nodes. The second approach is very
 * costly and would make redeclaring a deftemplate undesirable.
 */
public class Deftemplate implements Template, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected TemplateSlot[] slots;

	private boolean watch = false;

	private String templateName = null;

	private Template parent = null;

	private String description = null;

	protected List<Tag> tags;

	/**
	 * Defclass and Deftemplate are decoupled, so it uses a string to look up
	 * the Defclass rather than have a link to it. This is because the
	 * reflection classes are not serializable.
	 */
	private String defclass = null;

	public Deftemplate(final String name, final String defclass,
			final TemplateSlot[] slots) {
		templateName = name;
		this.defclass = defclass;
		this.slots = slots;
		tags = new ArrayList<Tag>();

	}

	public Deftemplate(final String name, final String defclass,
			final TemplateSlot[] slots, final Template parent) {
		this(name, defclass, slots);
		this.parent = parent;
	}

	public Deftemplate(final String name) {
		this(name, null, null);
	}

	public Deftemplate(final String name, final Template parent) {
		this(name, null, null, parent);
	}

	public Deftemplate() {
		this(null, null, null, null);
	}

	/**
	 * checkName will see if the user defined the module to declare the
	 * template. if it is, it will create the module and return it.
	 * 
	 * @param engine
	 * @return
	 */
	public Module checkName(final Engine engine) {
		if (templateName.indexOf("::") > 0) {
			final String[] sp = templateName.split("::");
			templateName = sp[1];
			return engine.getModules().getModule(sp[0]);
		} else
			return null;
	}

	public Template getParent() {
		return parent;
	}

	public void setParent(final Template parent) {
		this.parent = parent;
	}

	/**
	 * return whether the deftemplate should be watched
	 * 
	 * @return
	 */
	public boolean getWatch() {
		return watch;
	}

	/**
	 * set whether the deftemplate should be watched
	 * 
	 * @param watch
	 */
	public void setWatch(final boolean watch) {
		this.watch = watch;
	}

	/**
	 * the template name is an alias for an object
	 * 
	 * @param name
	 */
	public String getName() {
		return templateName;
	}

	/**
	 * Get the class the deftemplate represents
	 * 
	 * @return
	 */
	public String getClassName() {
		return defclass;
	}

	/**
	 * Return the number of slots in the deftemplate
	 * 
	 * @return
	 */
	public int getNumberOfSlots() {
		return slots.length;
	}

	/**
	 * Return all the slots
	 * 
	 * @return
	 */
	public TemplateSlot[] getAllSlots() {
		return slots;
	}

	/**
	 * A convienance method for finding the slot matching the String name.
	 * 
	 * @param name
	 * @return
	 */
	public TemplateSlot getSlot(final String name) {
		for (int idx = 0; idx < slots.length; idx++)
			if (slots[idx].getName().equals(name))
				return slots[idx];
		return null;
	}

	/**
	 * get the Slot at the given column id
	 * 
	 * @param id
	 * @return
	 */
	public TemplateSlot getSlot(final int id) {
		return slots[id];
	}

	public Fact createFact(final SlotConfiguration[] scs, final Engine engine)
			throws EvaluationException {
		Boolean foundSlotMatching = false;
		SlotConfiguration sc = null;
		Slot slot = null;

		final Slot[] slots = createFactSlots(engine);

		final ArrayList<Slot> bslots = new ArrayList<Slot>();

		boolean hasbinding = false;
		for (int i = 0; i < scs.length; i++) {
			// initialize foundslotMatching to false:
			foundSlotMatching = false;

			sc = scs[i];
			for (int j = 0; j < slots.length; j++) {
				slot = slots[j];

				// template slots name matches SlotConfiguration Name?
				if (slot.getName().equals(sc.getSlotName())) {
					// we found matching slots for our sc
					foundSlotMatching = true;

					// copy slot id:
					slot.setId(sc.getId());

					JamochaValue val = sc.getValue(engine);
					// Multislot?
					if (slot instanceof MultiSlot) {
						// check the list to see if there's any bindings
						if (!val.is(JamochaType.LIST)) {
							final JamochaValue[] values = { val };
							val = JamochaValue.newList(values);
						}
						for (int mdx = 0; mdx < val.getListCount(); mdx++) {
							final JamochaValue v2 = val.getListValue(mdx);
							if (v2.getType() == JamochaType.BINDING) {
								final Slot clone = (Slot) slot.clone();
								clone.setValue(val);
								bslots.add(clone);
								hasbinding = true;
								break;
							}
						}
						slot.setValue(val);
					} else // no multislot:
					if (val == null)
						slot.setValue(JamochaValue.NIL);
					else if (val.getType() == JamochaType.BINDING) {
						slot.setValue(val);
						final Slot clone = (Slot) slot.clone();
						clone.setValue(val);
						bslots.add(clone);
						hasbinding = true;
					} else
						slot.setValue(val);
					break;
				}
			}
			// did we find a matching slot for our slotconfiguration?
			if (foundSlotMatching == false)
				throw new AssertException(
						"Could not find a slot for given slotname "
								+ sc.getSlotName() + " in Template "
								+ templateName + ".");
		}

		// check slots with required values "(default ?NONE)" for empty asserts
		TemplateSlot ts = null;

		for (int i = 0; i < getAllSlots().length; i++) {
			ts = this.getSlot(i);

			if (ts.isRequired()) {
				final String slotName = ts.getName();

				for (int j = 0; j < slots.length; j++)
					if (slots[j].getName().equals(slotName)
							&& slots[j].getValue().equals(JamochaValue.NIL))
						throw new AssertException(
								"A non-empty value is required for the slot "
										+ ts.getName()
										+ " by the corresponding template"
										+ templateName + ".");
			}
		}

		final Deffact newfact = new Deffact(this, slots);
		if (hasbinding) {
			final Slot[] slts2 = new Slot[bslots.size()];
			newfact.boundSlots = bslots.toArray(slts2);
			newfact.hasBinding = true;
		}
		// we call this to create the string used to map the fact.
		newfact.equalityIndex();
		return newfact;
	}

	public Fact createTemporalFact(final Object[] data, final long id,
			final Engine engine) throws EvaluationException {
		final Slot[] values = createFactSlots(engine);
		long expire = 0;
		String source = "";
		String service = "";
		long valid = 0;
		for (int idz = 0; idz < data.length; idz++) {
			final Slot s = (Slot) data[idz];
			// check to see if the slot is a temporal fact attribute
			if (isTemporalAttribute(s)) {
				if (s.getName().equals(TemporalFact.EXPIRATION))
					expire = s.getValue().getLongValue();
				else if (s.getName().equals(TemporalFact.SERVICE_TYPE))
					service = s.getValue().getStringValue();
				else if (s.getName().equals(TemporalFact.SOURCE))
					source = s.getValue().getStringValue();
				else if (s.getName().equals(TemporalFact.VALIDITY))
					valid = s.getValue().getLongValue();
			} else
				for (int idx = 0; idx < values.length; idx++)
					if (values[idx].getName().equals(s.getName()))
						if (s.value == null)
							values[idx].value = JamochaValue.NIL;
						else
							values[idx].value = s.value;
		}
		final TemporalDeffact newfact = new TemporalDeffact(this, values, id);
		// we call this to create the string used to map the fact.
		newfact.setExpirationTime(expire);
		newfact.setServiceType(service);
		newfact.setSource(source);
		newfact.setValidity((int) valid);
		newfact.equalityIndex();
		return newfact;
	}

	public static boolean isTemporalAttribute(final Slot s) {
		if (s.getName().equals(TemporalFact.EXPIRATION)
				|| s.getName().equals(TemporalFact.SERVICE_TYPE)
				|| s.getName().equals(TemporalFact.SOURCE)
				|| s.getName().equals(TemporalFact.VALIDITY))
			return true;
		else
			return false;
	}

	/**
	 * Create the facts for the slots
	 * 
	 * @return
	 * @throws EvaluationException
	 */
	private Slot[] createFactSlots(final Engine engine)
			throws EvaluationException {
		final Slot[] factSlots = new Slot[slots.length];
		for (int idx = 0; idx < factSlots.length; idx++)
			factSlots[idx] = slots[idx].createSlot(engine);
		return factSlots;
	}

	/**
	 * If any slot has a usecount greater than 0, we return true.
	 */
	public boolean inUse() {
		for (int idx = 0; idx < slots.length; idx++)
			if (slots[idx].getNodeCount() > 0)
				return true;
		return false;
	}

	/**
	 * Method will return a string format with the int type code for the slot
	 * type
	 */
	@Override
	public String toString() {
		return format(ParserFactory.getFormatter());
	}

	/**
	 * Method will generate a pretty printer format of the Deftemplate
	 * 
	 * @return
	 */
	public String toPPString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("(" + templateName + Constants.LINEBREAK);
		for (int idx = 0; idx < slots.length; idx++)
			buf.append("  (" + slots[idx].getName() + " (type "
					+ slots[idx].getValueType() + ") )" + Constants.LINEBREAK);
		if (defclass != null)
			buf.append("[" + defclass + "] ");
		buf.append(")");
		return buf.toString();
	}

	/**
	 * TODO - need to finish implementing this
	 */
	public Deftemplate cloneDeftemplate() {
		final Deftemplate dt = new Deftemplate(templateName, defclass, slots);

		return dt;
	}

	public void evaluateStaticDefaults(final Engine engine)
			throws EvaluationException {
		for (int i = 0; i < slots.length; ++i)
			if (slots[i].isStaticDefault()) {
				final JamochaValue constantValue = slots[i]
						.getDefaultExpression().getValue(engine);
				slots[i].setStaticDefaultExpression(constantValue);
			}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDump(final String modName) {
		final StringBuffer buf = new StringBuffer();
		buf.append("(deftemplate " + modName + "::" + templateName + " ");
		for (int idx = 0; idx < slots.length; idx++) {
			buf.append("(slot " + slots[idx].getName());
			if (slots[idx].getValueType() != JamochaType.UNDEFINED)
				buf.append(" (type " + slots[idx].getValueType() + ") ");
			buf.append(") ");
		}
		if (defclass != null)
			buf.append("[" + defclass + "] ");
		buf.append(")");
		return buf.toString();
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	public Iterator<Tag> getTags() {
		return getTags(Tag.class);
	}

	public void addTag(final Tag t) {
		tags.add(t);
	}

	public Iterator<Tag> getTags(final Class<? extends Tag> tagClass) {
		return new TagIterator(tagClass, tags);
	}

	/**
	 * gets the text-dump for that template
	 */
	public String getDump() {
		return toString();
	}
}
