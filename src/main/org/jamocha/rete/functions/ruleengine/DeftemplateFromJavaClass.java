/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden, Uta Christoph
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
package org.jamocha.rete.functions.ruleengine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.jsr94.internal.BeanTemplate2JavaClassAdaptor;
import org.jamocha.jsr94.internal.PublicAttributesTemplate2JavaClassAdaptor;
import org.jamocha.jsr94.internal.Template2JavaClassAdaptor;
import org.jamocha.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.wme.Deffact;
import org.jamocha.rete.wme.Deftemplate;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rete.wme.Slot;
import org.jamocha.rete.wme.Template;
import org.jamocha.rete.wme.TemplateSlot;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class DeftemplateFromJavaClass extends AbstractFunction {

	private static final class Description implements
			FunctionDescription {

		public String getDescription() {
			return "Defines a new template out of a Java class.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Whether this function should use the getter/setter-defined values (true) or simply the public attributes (false)";
			case 1:
				return "The fully qualified java class name";
			}
			return null;
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "bean-style";
			case 1:
				return "classname";
			}
			return null;
		}

		public JamochaType[] getParameterTypes(int parameter) {
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

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(deftemplate-from-javaclass true org.jamocha.jsr94.test.Wurst)";
		}

		public boolean isResultAutoGeneratable() {
			return false;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "deftemplate-from-javaclass";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}
	
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		if (params != null && params.length == 2) {
			boolean beanStyle = params[0].getValue(engine).getBooleanValue();
			String javaClass = params[1].getValue(engine).getStringValue();
			
			// check, whether such a template already exists
			Template t = engine.getCurrentFocus().getTemplate(javaClass);
			if (t != null) return JamochaValue.FALSE;
			
			// get the class from the parameter string
			Class cls;
			try {
				cls = Class.forName(javaClass);
			} catch (ClassNotFoundException e) {
				engine.writeMessage("class not found");
				return JamochaValue.FALSE;
			}
			
			// determine slots
			Template2JavaClassAdaptor adaptor = null;
			if (beanStyle) {
				adaptor = BeanTemplate2JavaClassAdaptor.getAdaptor();
			} else {
				adaptor = PublicAttributesTemplate2JavaClassAdaptor.getAdaptor();
			}
			
			List<TemplateSlot> slots = new ArrayList<TemplateSlot>();
			int id = 0;
			try {
				for (String slotName : adaptor.getFields(cls)) {
					//TODO handle data type
					TemplateSlot tslot = new TemplateSlot(slotName);
					tslot.setId(id++);
					slots.add(tslot);
				}
			} catch (Template2JavaClassAdaptorException e) {
				throw new EvaluationException("error while getting fields",e);
			}
			
			TemplateSlot[] slotsArray = new TemplateSlot[slots.size()];
			slotsArray = slots.toArray(slotsArray);
			Deftemplate newtempl = new Deftemplate(cls.getCanonicalName(),"",slotsArray);
			newtempl.addTag( new TemplateFromJavaClassTag(cls,adaptor));
			return engine.addTemplate(newtempl) ? JamochaValue.TRUE : JamochaValue.FALSE;
		} else {
			throw new IllegalParameterException(1);
		}
	}
}