/*
 * Copyright 2007 Josef Alexander Hahn, Alexander Wilden, Uta Christoph
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.util.ExportHandler;
import org.jamocha.rete.util.ExportIterator;

/**
 * @author Josef Alexander Hahn
 * 
 * Exports facts from the rete engine to an external location. The external location is 
 * specified in the first argument and needs to be an user-implementation of org.jamocha.rete.util.ExportHandler.
 * The function first generates a mapping from the config-fact given in the second argument. 
 * After that it exports all facts from the facts-list in the third argument through the iterator.
 * 
 * Former developers description:
 * Exports facts from the rete engine to an external location. This external location needs 
 * to be an user-implementation of org.jamocha.rete.util.ExportHandler.
 * This interface defines the function 
 * long export(java.util.Iterator<org.jamocha.rete.Deffact>, java.util.Map<String,String>).
 * When calling iteratorexporter, similar to iteratorimporter, it creates an instance of an 
 * ExportHandler-subclass. Then it generates a map from a given config-fact. 
 * After all, it calls export.
 */
public class IteratorExporter extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Exports facts from the rete engine to an external location. The external location is specified in the " +
					"first argument and needs to be an user-implementation of a subclass of org.jamocha.rete.util.ExportHandler. " +
					"The function first generates a mapping from the config-fact given in the second argument. After " +
					"that it exports all facts from the fact-list given as third argument via the iterator.";					
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "External location, class name of a subclass of org.jamocha.rete.util.ExportHandler.";
			case 1:
				return "Config-fact, to generate the mapping for the export from.";
			case 2:
				return "Fact-list to export via the iterator.";// These facts are piped to the Iterator.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "ExportHandlerClass";
			case 1:
				return "ConfigFact";
			case 2:
				return "FactsToExport";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.FACT_IDS;
			case 2:
				return JamochaType.LISTS;
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
			return "(deftemplate a 	(slot horst))\n" +
					"(deftemplate b	(slot heiner))\n" +
					"(deftemplate c	(slot ory))\n" +
					"(deftemplate d	(slot krautsalat))\n" +
					"(bind ?horst (assert (a (horst 1))))\n" +
					"(bind ?heiner1 (assert	(b (heiner 13))))\n" +
					"(bind ?heiner2	(assert	(b (heiner 1))))\n" +
					"(bind ?ory	(assert	(c (ory 4711))))\n" +
					"(bind ?krautsalat (assert (d (krautsalat 11))))\n" +
					"(deftemplate config (slot removeSlot))\n" +
					"(bind ?config (assert (config (removeSlot \"heiner\"))))\n" +
					"(iteratorexporter \"org.jamocha.sampleimplementations.SampleExportHandler\"  ?config (create$ ?horst ?heiner1 ?ory ?krautsalat))";
		}

		public boolean isResultAutoGeneratable() {			
			return true;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "iteratorexporter";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	@SuppressWarnings("unchecked")
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {

		if (params != null) {
			if (params.length == 3) {

				/*
				 * try to load the class given by first parameter and return
				 * false in case of failure
				 */
				Class handlerclass = null;

				try {
					handlerclass = Class.forName(params[0].getValue(engine)
							.getStringValue());
				} catch (ClassNotFoundException e1) {
					return new JamochaValue(JamochaType.LONG, new Long(-1));
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

				/* instantiate the given ExportHandler class */
				ExportHandler handler = null;
				try {
					handler = (ExportHandler) handlerclass.newInstance();
				} catch (Exception e1) {
					return new JamochaValue(JamochaType.LONG, new Long(-1));
				}

				JamochaValue forExport = params[2].getValue(engine);
				long[] facts = new long[forExport.getListCount()];
				for (int i = 0; i < forExport.getListCount(); i++) {
					facts[i] = forExport.getListValue(i).getFactIdValue();
				}

				Iterator<Deffact> iterator = new ExportIterator(engine, facts);

				/*
				 * well, now we have an ExportHandler, an iterator and a map.
				 * lets put it together in the ExportHandler
				 */

				long result = handler.export(iterator, configMap);
				return new JamochaValue(JamochaType.LONG, new Long(result));
			}
		}
		throw new IllegalParameterException(3, false);
	}
}
