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

package org.jamocha.engine.functions.ruleengine;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.communication.jsr94.internal.BeanTemplate2JavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.PublicAttributesTemplate2JavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.communication.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.communication.logging.Logging;
import org.jamocha.communication.logging.Logging.JamochaLogger;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class DeftemplateFromJavaClass extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Defines a new template out of a Java class.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(final int parameter) {
			switch (parameter) {
			case 0:
				return "Whether this function should use the getter/setter-defined values (true) or simply the public attributes (false)";
			case 1:
				return "The fully qualified java class name";
			}
			return null;
		}

		public String getParameterName(final int parameter) {
			switch (parameter) {
			case 0:
				return "bean-style";
			case 1:
				return "classname";
			}
			return null;
		}

		public JamochaType[] getParameterTypes(final int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.BOOLEANS;
			case 1:
				return JamochaType.STRINGS;
			}
			return null;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(final int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate-from-javaclass true org.jamocha.jsr94.test.Wurst)";
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "deftemplate-from-javaclass";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(final Engine engine,
			final Parameter[] params) throws EvaluationException {
		final JamochaLogger log = Logging.logger(this.getClass());
		if (params != null && params.length == 2) {

			final boolean beanStyle = params[0].getValue(engine)
					.getBooleanValue();
			final String javaClass = params[1].getValue(engine)
					.getStringValue();

			log.debug("Trying to generate deftemplate from '"
					+ javaClass
					+ "'"
					+ (beanStyle ? " by bean attributes"
							: " directly by public members"));

			// check, whether such a template already exists
			final Template t = engine.getCurrentFocus().getTemplate(javaClass);
			if (t != null) {
				log.warn("a deftemplate '" + javaClass + "' already exists");
				return JamochaValue.FALSE;
			}

			// get the class from the parameter string
			Class<? extends Object> cls;
			try {
				cls = Class.forName(javaClass, true, Thread.currentThread()
						.getContextClassLoader());
			} catch (final ClassNotFoundException e) {
				log.warn("class '" + javaClass + "' not found");
				engine.writeMessage("class not found");
				return JamochaValue.FALSE;
			}
			log.debug("found class '" + javaClass + "'");

			// determine slots
			Template2JavaClassAdaptor adaptor = null;
			if (beanStyle)
				adaptor = BeanTemplate2JavaClassAdaptor.getAdaptor();
			else
				adaptor = PublicAttributesTemplate2JavaClassAdaptor
						.getAdaptor();

			final List<TemplateSlot> slots = new ArrayList<TemplateSlot>();
			int id = 0;
			try {
				for (final String slotName : adaptor.getFields(cls)) {
					// TODO handle data type
					final TemplateSlot tslot = new TemplateSlot(slotName);
					tslot.setId(id++);
					log.debug("in class '" + cls.getCanonicalName()
							+ "' found attribute '" + slotName + "'");
					slots.add(tslot);
				}
			} catch (final Template2JavaClassAdaptorException e) {
				throw new EvaluationException("error while getting fields", e);
			}

			TemplateSlot[] slotsArray = new TemplateSlot[slots.size()];
			slotsArray = slots.toArray(slotsArray);
			final Deftemplate newtempl = new Deftemplate(
					cls.getCanonicalName(), "", slotsArray);
			newtempl.addTag(new TemplateFromJavaClassTag(cls, adaptor));

			if (engine.addTemplate(newtempl)) {
				log.debug("deftemplate '" + newtempl.getName()
						+ "' successfully defined");
				return JamochaValue.TRUE;
			} else {
				log.warn("error while defining deftemplate '" + cls.getName()
						+ "'");
				return JamochaValue.FALSE;
			}

		} else
			throw new IllegalParameterException(1);
	}
}