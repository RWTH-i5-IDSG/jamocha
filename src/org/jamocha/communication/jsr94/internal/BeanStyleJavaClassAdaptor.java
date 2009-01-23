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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> This
 *         Template2JavaClassAdaptor works on Java-Beans, but its somewhat
 *         limited: It will only "see" attributes, which have a getter (without
 *         parameters) and a setter (with exactly one parameter with the right
 *         type). This is important, since we must be able to read and write to
 *         each attribute. Maybe, you've so-called "immutable" classes, with
 *         only getters and a constructor for setting the attributes. This is to
 *         nearly impossible to automatically evaluate via the reflections api.
 *         So, in that case, you've to provide setters e.g. via an
 *         adaptor-class...
 */
public class BeanStyleJavaClassAdaptor implements JavaClassAdaptor {

	protected static BeanStyleJavaClassAdaptor instance = null;

	public static BeanStyleJavaClassAdaptor getAdaptor() {
		if (instance == null)
			instance = new BeanStyleJavaClassAdaptor();
		return instance;
	}

	protected BeanStyleJavaClassAdaptor() {
	}

	// TODO document interface!
	// TODO implement cloning from facts and templates with tags

	public Fact getFactFromObject(final Object o, final Engine engine)
			throws Template2JavaClassAdaptorException {
		// TODO one can implement that much more efficient
		final Template t = engine.getCurrentFocus().getTemplate(
				o.getClass().getCanonicalName());
		final List<Slot> valuesList = new LinkedList<Slot>();
		for (final String attr : getFields(o.getClass()))
			try {
				final Slot s = t.getSlot(attr).createSlot(engine);
				s.setValue(JamochaValue
						.newValueAutoType(getFieldValue(attr, o)));
				valuesList.add(s);
			} catch (final Exception e) {
				throw new Template2JavaClassAdaptorException(
						"error while generating a fact from an object", e);
			}
		Slot[] values = new Slot[valuesList.size()];
		values = valuesList.toArray(values);
		final Fact newFact = new Deffact(t, values);
		return newFact;
	}

	protected String upperCaseFirstLetter(final String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	protected String lowerCaseFirstLetter(final String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	protected Object getFieldValue(final String field, final Object o,
			final String prefix) throws Template2JavaClassAdaptorException {
		final String fname = upperCaseFirstLetter(field);
		try {
			final Method m = o.getClass().getMethod(prefix + fname);
			return m.invoke(o);
		} catch (final Exception e) {
			throw new Template2JavaClassAdaptorException(
					"error while getting the value from field '" + field
							+ "' in class " + o.getClass().getCanonicalName(),
					e);
		}
	}

	public Object getFieldValue(final String field, final Object o)
			throws Template2JavaClassAdaptorException {
		try {
			return getFieldValue(field, o, "get");
		} catch (final Template2JavaClassAdaptorException e) {
			return getFieldValue(field, o, "is");
		}
	}

	public List<String> getFields(final Class<? extends Object> c) {
		final List<String> fields = new ArrayList<String>();
		for (final Method getter : c.getMethods())
			if (getter.getName().startsWith("get")
					|| getter.getName().startsWith("is")) {
				// collect some information
				String potetialAttrName;
				if (getter.getName().startsWith("get"))
					potetialAttrName = getter.getName().substring(3);
				else
					potetialAttrName = getter.getName().substring(2);
				if (getter.getParameterTypes().length > 0)
					continue;
				final Class<? extends Object> attrType = getter.getReturnType();
				// search for a setter
				Method setter;
				try {
					setter = c.getMethod("set" + potetialAttrName, attrType);
				} catch (final SecurityException e) {
					// TODO better exception handling
					continue;
				} catch (final NoSuchMethodException e) {
					// TODO better exception handling
					continue;
				}
				assert setter != null;
				fields.add(lowerCaseFirstLetter(potetialAttrName));
			}
		return fields;
	}

	protected Method getSetter(final Class<? extends Object> c,
			final String attr) throws Template2JavaClassAdaptorException {
		// TODO we should cache the results
		for (final Method o : c.getMethods()) {
			final String mname = o.getName();
			if (!mname.startsWith("set"))
				continue;
			if (!mname.substring(3).equals(upperCaseFirstLetter(attr)))
				continue;
			if (o.getParameterTypes().length != 1)
				continue;
			return o;
		}
		throw new Template2JavaClassAdaptorException(
				"no setter found for attribute '" + attr + "' in class '"
						+ c.getCanonicalName() + "'");
	}

	protected void setField(final String attr, final Object o,
			final JamochaValue value) throws IllegalArgumentException,
			IllegalAccessException, Template2JavaClassAdaptorException,
			InvocationTargetException {
		Object v = value.getObjectValue();
		final Method setter = getSetter(o.getClass(), attr);
		final Class<? extends Object> fieldType = setter.getParameterTypes()[0];
		if (v instanceof Long
				&& (fieldType == int.class || fieldType == Integer.class))
			v = new Integer(((Long) v).intValue());
		if (v instanceof Double
				&& (fieldType == float.class || fieldType == Float.class))
			v = new Float(((Double) v).floatValue());
		setter.invoke(o, v);
	}

	// TODO: what does the "c" here?
	public Object storeToObject(final Fact f, final Object o, final Engine engine)
			throws Template2JavaClassAdaptorException {
		final Iterator<Tag> titr = f.getTemplate().getTags(
				TemplateFromJavaClassTag.class);
		Class<? extends Object> c = null;
		if (titr.hasNext()) {
			final TemplateFromJavaClassTag t = (TemplateFromJavaClassTag) titr
					.next();
			c = t.getJavaClass();
			for (final String attr : getFields(o.getClass()))
				try {
					final JamochaValue value = f.getSlotValue(attr);
					setField(attr, o, value);
				} catch (final Exception e) {
					throw new Template2JavaClassAdaptorException(
							"error while storing fact to object", e);
				}
		}
		return null;
	}

}
