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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.ValueParam;
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
				
				Class factoryclass = null;
				try {
					factoryclass = Class.forName(params[0].getValue(engine).getStringValue());
				} catch (ClassNotFoundException e1) {
					return new JamochaValue(JamochaType.BOOLEAN, Boolean.FALSE);
				}
				Fact configFact = engine.getFactById(params[1].getValue(engine).getFactIdValue());
				
				Deftemplate configtemplate = configFact.getDeftemplate();
				Slot[] keys = configtemplate.getAllSlots();
				Map<String,String> configMap = new HashMap<String,String>();
				for ( Slot key : keys) {
					configMap.put( key.getName() , configFact.getSlotValue(key.getName()).getStringValue() );
				}
				
				Constructor<DeffactIterator> ourConstructor = null;
				for ( Constructor<DeffactIterator> c : factoryclass.getConstructors() ) {
					if (c.getParameterTypes().length == 1){
						if (c.getParameterTypes()[0].isAssignableFrom( Map.class )) {
							ourConstructor = c;
						}
					}
				}
				
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
			}
			return new JamochaValue(JamochaType.BOOLEAN, Boolean.TRUE);
		}
		throw new IllegalParameterException(2, false);
	}

	public String getName() {
		return ITERATORIMPORTER;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class , ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length == 3) {
			StringBuffer buf = new StringBuffer();
			buf.append("(iteratorimporter");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getExpressionString());
				} else {
					buf.append(" " + params[idx].getExpressionString());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(iteratorimporter <string> <fact-id>\n" 
					+ "Function description:\n"
					+ "\t first parameter is a class name of a subclass of DeffactIterator\n"
					+ "\t second parameter is a fact-id from a configuration-fact for the iterator\n"
					+ "\t it iterates over a new instance of the given iterator-class and generates"
					+ "\t facts of them. it returns true iff everything was successfull.";
		}
	}
}
