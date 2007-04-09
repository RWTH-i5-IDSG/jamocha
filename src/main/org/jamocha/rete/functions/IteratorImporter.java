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
package org.jamocha.rete.functions;

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
import org.jamocha.rete.util.DeffactIterator;

/**
 * @author Josef Alexander Hahn
 * 
 */
public class IteratorImporter implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ITERATORIMPORTER = "iteratorimporter";

	/**
	 * 
	 */
	public IteratorImporter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {

		if (params != null) {
			if (params.length == 2) {
				
				/* try to load the class given by first parameter and
				 * return false in case of failure */
				Class iteratorclass = null;
				try {
					iteratorclass = Class.forName(params[0].getValue(engine).getStringValue());
				} catch (ClassNotFoundException e1) {
					return new JamochaValue(JamochaType.BOOLEAN, Boolean.FALSE);
				}
				
				/* load the configure fact and generate a hashmap (name/value pairs)
				 * from the slots */
				Fact configFact = engine.getFactById(params[1].getValue(engine).getFactIdValue());
				Template configtemplate = configFact.getTemplate();
				Slot[] keys = configtemplate.getAllSlots();
				Map<String,String> configMap = new HashMap<String,String>();
				for ( Slot key : keys) {
					configMap.put( key.getName() , configFact.getSlotValue(key.getName()).getStringValue() );
				}
				
				/* ...and now for something completely different ;) try to get the right constructor from
				 * our iteratorclass (this one with only one map<string,string> parameter) */
				Constructor<DeffactIterator> ourConstructor = null;
				for ( Constructor<DeffactIterator> c : iteratorclass.getConstructors() ) {
					if (c.getParameterTypes().length == 1){
						if (c.getParameterTypes()[0].isAssignableFrom(Map.class)) {
							ourConstructor = c;
						}
					}
				}
				/* no good constructor found => return false */
				if (ourConstructor == null) return new JamochaValue(JamochaType.BOOLEAN, Boolean.FALSE);
				
				/* well, now we have an iteratorclass, a constructor and a map. lets put
				 * it together to get an iterator
				 * and assert the facts returned by the iterator... */
				Object[] constructorparams = new Object[1];
				constructorparams[0] = configMap;
				DeffactIterator ourIterator = null;
				try {
					ourIterator = ourConstructor.newInstance(constructorparams);
					while (ourIterator.hasNext()  ) {
						engine.assertFact(ourIterator.next());
					}
				} catch (Exception e) {
					return new JamochaValue(JamochaType.BOOLEAN, Boolean.FALSE);
				}
				/* if we have reached this point of code, everything is fine */
				return new JamochaValue(JamochaType.BOOLEAN, Boolean.TRUE);
			}
		}
		throw new IllegalParameterException(2, false);
	}

	public String getName() {
		return ITERATORIMPORTER;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length == 3) {
			StringBuffer buf = new StringBuffer();
			buf.append("(iteratorimporter");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" " + params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(iteratorimporter <string> <fact-id>)\n" 
					+ "Function description:\n"
					+ "\t first parameter is a class name of a subclass of DeffactIterator\n"
					+ "\t second parameter is a fact-id from a configuration-fact for the iterator\n"
					+ "\t it iterates over a new instance of the given iterator-class and generates\n"
					+ "\t facts of them. it returns true iff everything was successful.\n";
		}
	}
}
