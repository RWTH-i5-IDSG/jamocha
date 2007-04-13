/*
 * Copyright 2007 Josef Alexander Hahn, Alexander Wilden
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
package org.jamocha.rete.functions.adaptor;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.util.DeffactIterator;

/**
 * @author Josef Alexander Hahn
 * 
 * TODO please add a description!
 */
public class IteratorImporter implements Function, Serializable {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "TODO";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "TODO";
			case 1:
				return "TODO";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "unknown";
			case 1:
				return "unknown";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.FACT_IDS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.LONGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "iteratorimporter";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {

		if (params != null) {
			if (params.length == 2) {

				/*
				 * try to load the class given by first parameter and return
				 * false in case of failure
				 */
				Class iteratorclass = null;
				try {
					iteratorclass = Class.forName(params[0].getValue(engine)
							.getStringValue());
				} catch (ClassNotFoundException e1) {
					return JamochaValue.FALSE;
				}

				/*
				 * load the configure fact and generate a hashmap (name/value
				 * pairs) from the slots
				 */
				Fact configFact = engine.getFactById(params[1].getValue(engine)
						.getFactIdValue());
				Template configtemplate = configFact.getTemplate();
				Slot[] keys = configtemplate.getAllSlots();
				Map<String, String> configMap = new HashMap<String, String>();
				for (Slot key : keys) {
					configMap.put(key.getName(), configFact.getSlotValue(
							key.getName()).getStringValue());
				}

				/*
				 * ...and now for something completely different ;) try to get
				 * the right constructor from our iteratorclass (this one with
				 * only one map<string,string> parameter)
				 */
				Constructor<DeffactIterator> ourConstructor = null;
				for (Constructor<DeffactIterator> c : iteratorclass
						.getConstructors()) {
					if (c.getParameterTypes().length == 1) {
						if (c.getParameterTypes()[0]
								.isAssignableFrom(Map.class)) {
							ourConstructor = c;
						}
					}
				}
				/* no good constructor found => return false */
				if (ourConstructor == null)
					return JamochaValue.FALSE;

				/*
				 * well, now we have an iteratorclass, a constructor and a map.
				 * lets put it together to get an iterator and assert the facts
				 * returned by the iterator...
				 */
				Object[] constructorparams = new Object[1];
				constructorparams[0] = configMap;
				DeffactIterator ourIterator = null;
				try {
					ourIterator = ourConstructor.newInstance(constructorparams);
					while (ourIterator.hasNext()) {
						engine.assertFact(ourIterator.next());
					}
				} catch (Exception e) {
					return JamochaValue.FALSE;
				}
				/* if we have reached this point of code, everything is fine */
				return JamochaValue.TRUE;
			}
		}
		throw new IllegalParameterException(2, false);
	}
}