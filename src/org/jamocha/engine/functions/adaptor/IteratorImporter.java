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

package org.jamocha.engine.functions.adaptor;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.util.DeffactIterator;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;

/**
 * @author Josef Alexander Hahn
 * 
 * Imports facts from an external, user-defined iterator to the rete engine. The
 * external iterator is specified in the first argument and needs to be a
 * user-implementation of a subclass of org.jamocha.rete.util.DeffactIterator.
 * The function generates a mapping from the config-fact given in the second
 * argument. After that it asserts a fact from each deffact the iterator
 * returns.
 * 
 * Former developers description: Imports the content from an user-written
 * (special) iterator and asserts a fact for each Deffact, the iterator returns.
 * The user has to write a subclass of org.jamocha.rete.util.DeffactIterator.
 * This class is a subclass from java.util.Iterator<org.jamocha.rete.Deffact>
 * defining an additional public constructor DeffactIterator(java.util.Map<String,String>).
 * This constructor is able to receive additional information through the given
 * map. It returns true, iff everything went fine.
 */
public class IteratorImporter extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Imports facts from an external, user-defined iterator to the rete engine. The external iterator is"
					+ "specified in the first argument and needs to be a user-implementation of a subclass of org.jamocha.rete.util.DeffactIterator.  "
					+ "The function generates a mapping from the config-fact given in the second argument. After that it"
					+ "asserts a fact from each deffact the iterator returns. Returns true, iff everything went fine.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "User-defined iterator, class name of a subclass of org.jamocha.rete.util.DeffactIterator.";
				// "The first argument is the (fully qualified) class name of
				// the DeffactIterator-subclass. " +
				// "This class will be used for getting facts. There is a sample
				// implementation " +
				// "org.jamocha.sampleimplementations.DeffactFibonacciIterator,
				// which will put out facts
				// containing fibonacci numbers.";
			case 1:
				return "Config-fact, to generate a mapping for the import from.";
				// this fact will be used for giving some addition information
				// to the iterator.
				// Therefrom a map will be generated and used in the constructor
				// call. for now, all slots must
				// be string-typed. The concrete slots will differ for each
				// class and are user-defined, too.
				// The sample DeffactFibonacciIterator needs a slot max, which
				// sets the maximum value.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "DeffactIteratorClass";
			case 1:
				return "ConfigFact";
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

		public String getExample() {
			return "(deftemplate fibonacciconfig (slot max))\n"
					+ "(bind ?config	(assert	(fibonacciconfig (max \"1000\"))))\n"
					+ "(iteratorimporter \"org.jamocha.sampleimplementations.DeffactFibonacciIterator\" ?config)\n"
					+ "(facts)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "iteratorimporter";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {

		if (params != null)
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
				for (Slot key : keys)
					configMap.put(key.getName(), configFact.getSlotValue(
							key.getName()).getStringValue());

				/*
				 * ...and now for something completely different ;) try to get
				 * the right constructor from our iteratorclass (this one with
				 * only one map<string,string> parameter)
				 */
				Constructor<DeffactIterator> ourConstructor = null;
				for (Constructor<DeffactIterator> c : iteratorclass
						.getConstructors())
					if (c.getParameterTypes().length == 1)
						if (c.getParameterTypes()[0]
								.isAssignableFrom(Map.class))
							ourConstructor = c;
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
					while (ourIterator.hasNext())
						engine.assertFact(ourIterator.next());
				} catch (Exception e) {
					return JamochaValue.FALSE;
				}
				/* if we have reached this point of code, everything is fine */
				return JamochaValue.TRUE;
			}
		throw new IllegalParameterException(2, false);
	}
}