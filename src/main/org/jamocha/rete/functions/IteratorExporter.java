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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.util.ExportHandler;
import org.jamocha.rete.util.ExportIterator;

/**
 * @author Josef Alexander Hahn
 * 
 */
public class IteratorExporter implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ITERATOREXPORTER = "iteratorexporter";

	/**
	 * 
	 */
	public IteratorExporter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		return JamochaType.LONG;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {

		if (params != null) {
			if (params.length == 3) {
				
				/* try to load the class given by first parameter and
				 * return false in case of failure */
				Class handlerclass = null;
				
				try {
					handlerclass = Class.forName(params[0].getValue(engine).getStringValue());
				} catch (ClassNotFoundException e1) {
					return new JamochaValue(JamochaType.LONG, new Long(-1));
				}
				
				/* load the configure fact and generate a hashmap (name/value pairs)
				 * from the slots */
				Fact configFact = engine.getFactById(params[1].getValue(engine).getFactIdValue());
				Deftemplate configtemplate = configFact.getDeftemplate();
				Slot[] keys = configtemplate.getAllSlots();
				Map<String,String> configMap = new HashMap<String,String>();
				for ( Slot key : keys) {
					configMap.put( key.getName() , configFact.getSlotValue(key.getName()).getStringValue() );
				}
				
				/* instantiate the given ExportHandler class */
				ExportHandler handler = null;
				try {
					handler = (ExportHandler) handlerclass.newInstance();
				} catch (Exception e1) {
					return new JamochaValue(JamochaType.LONG, new Long(-1));
				}
				
				JamochaValue forExport = params[2].getValue(engine);
				long[] facts = new long[forExport.getListCount()];
				for ( int i=0 ; i<forExport.getListCount() ; i++ ){
					facts[i] = forExport.getListValue(i).getFactIdValue();
				}
				
	
				Iterator<Deffact> iterator = new ExportIterator(engine, facts);
				
				/* well, now we have an ExportHandler, an iterator and a map. lets put
				 * it together in the ExportHandler */
			
				long result = handler.export(iterator, configMap);
				return new JamochaValue(JamochaType.LONG, new Long(result));
			}
		}
		throw new IllegalParameterException(3, false);
	}

	public String getName() {
		return ITERATOREXPORTER;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length == 3) {
			StringBuffer buf = new StringBuffer();
			buf.append("(iteratorexporter");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" " + params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(iteratorexporter blah)\n" 
					+ "See jamocha wiki\n";
		}
	}
}
