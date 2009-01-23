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
package org.jamocha.communication.jsr94.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.engine.Engine;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> This
 *         Template2JavaClassAdaptor works on the public attributes of a class.
 *         It directly accesses these attributes when reading and writing
 *         java-objects and when generating templates from a class.
 */
public class PublicAttributesStyleJavaClassAdaptor implements
		JavaClassAdaptor {

	protected static PublicAttributesStyleJavaClassAdaptor instance = null;

	public static PublicAttributesStyleJavaClassAdaptor getAdaptor() {
		if (instance == null)
			instance = new PublicAttributesStyleJavaClassAdaptor();
		return instance;
	}

	protected PublicAttributesStyleJavaClassAdaptor() {
	}

	public Fact getFactFromObject(Object o, Engine engine) {
		Template t = engine.getCurrentFocus().getTemplate(
				o.getClass().getCanonicalName());
		List<Slot> valuesList = new LinkedList<Slot>();
		for (Field f : o.getClass().getFields())
			try {
				Slot s = t.getSlot(f.getName()).createSlot(engine);
				s.setValue(JamochaValue.newValueAutoType(f.get(o)));
				valuesList.add(s);
			} catch (EvaluationException e) {
				return null;
			} catch (IllegalArgumentException e) {
				return null;
			} catch (IllegalAccessException e) {
				return null;
			}
		Slot[] values = new Slot[valuesList.size()];
		values = valuesList.toArray(values);
		Fact newFact = new Deffact(t, values);
		return newFact;
	}

	public Object getFieldValue(String field, Object o) {
		try {
			return o.getClass().getField(field).get(o);
		} catch (Exception e) {
			return null;
		}
	}

	public List<String> getFields(Class<? extends Object> c) {
		List<String> fields = new ArrayList<String>();
		for (Field f : c.getFields())
			fields.add(f.getName());
		return fields;
	}

	protected void setField(Field fi, Object o, JamochaValue value)
			throws IllegalArgumentException, IllegalAccessException {
		Object v = value.getObjectValue();
		if (v instanceof Long
				&& (fi.getType() == int.class || fi.getType() == Integer.class))
			v = new Integer(((Long) v).intValue());
		if (v instanceof Double
				&& (fi.getType() == float.class || fi.getType() == Float.class))
			v = new Float(((Double) v).floatValue());
		fi.set(o, v);
	}

	public Object storeToObject(Fact f, Object o, Engine engine) {
		Iterator<Tag> titr = f.getTemplate().getTags(
				TemplateFromJavaClassTag.class);
		Class<? extends Object> c = null;
		if (titr.hasNext()) {
			TemplateFromJavaClassTag t = (TemplateFromJavaClassTag) titr.next();
			c = t.getJavaClass();
			for (Field fi : c.getFields()) {
				String name = fi.getName();
				try {
					JamochaValue value = f.getSlotValue(name);
					setField(fi, o, value);
				} catch (Exception e) {
					// TODO better exception handling
				}
			}
		}
		return null;
	}

}
