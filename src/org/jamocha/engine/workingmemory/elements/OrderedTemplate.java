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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.engine.workingmemory.elements.tags.TagIterator;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Sebastian Reinartz, Alexander Wilden
 */
public class OrderedTemplate implements Template {

	private static final long serialVersionUID = 1L;

	private final TemplateSlot data;

	private String templateName = null;

	private boolean watch = false;

	protected List<Tag> tags;

	public OrderedTemplate(final String name) {
		templateName = name;
		data = new TemplateSlot(Constants.ORDERED_FACT_SLOT);
		data.setMultiSlot(true);
		tags = new ArrayList<Tag>();

	}

	public Fact createFact(final Parameter[] params, final Engine engine)
			throws EvaluationException {
		final Slot slot = new Slot(data.getName());
		slot.setId(0);

		Slot bslot;
		boolean hasbinding = false;

		final JamochaValue[] list = new JamochaValue[params.length];
		for (int i = 0; i < params.length; i++) {
			list[i] = params[i].getValue(engine);
			if (list[i].getType() == JamochaType.BINDING)
				hasbinding = true;
		}

		final JamochaValue val = JamochaValue.newList(list);
		slot.setValue(val);

		final Deffact newfact = new Deffact(this, new Slot[] { slot });
		if (hasbinding) {
			bslot = (Slot) slot.clone();
			bslot.setValue(val);
			newfact.boundSlots = new Slot[] { bslot };
			newfact.hasBinding = true;
		}

		// we call this to create the string used to map the fact.
		newfact.equalityIndex();
		return newfact;
	}

	public TemplateSlot[] getAllSlots() {
		final TemplateSlot[] res = new TemplateSlot[1];
		res[0] = data;
		return res;
	}

	public String getClassName() {
		return null;
	}

	public String getName() {
		return templateName;
	}

	public int getNumberOfSlots() {
		return 1;
	}

	public Template getParentTemplate() {
		return null;
	}

	public TemplateSlot getSlot(final String name) {
		if (name.equals(data.getName()))
			return data;
		return null;
	}

	public TemplateSlot getSlot(final int column) {
		if (column == 0)
			return data;
		return null;
	}

	public boolean getWatch() {
		return watch;
	}

	public boolean inUse() {
		if (data.getNodeCount() > 0)
			return true;
		return false;
	}

	public void setParent(final Template parent) {
	}

	public void setWatch(final boolean watch) {
		this.watch = watch;
	}

	public String toPPString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("(" + templateName + Constants.LINEBREAK);
		buf.append("  (" + data.getName() + " (type " + data.getValueType()
				+ ") )" + Constants.LINEBREAK);
		buf.append(")");
		return buf.toString();
	}

	public String getDump(final String modName) {
		return toPPString();
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	public String getDescription() {
		return "(implied)";
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

	public Module checkUserDefinedModuleName(Engine engine) {
		// TODO Auto-generated method stub
		return null;
	}

	public void evaluateStaticDefaults(Engine engine) throws EvaluationException {
		// TODO Auto-generated method stub
	}
}
