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
package org.jamocha.rete.functions.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.modules.Module;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn
 * 
 * Writes engine's deftemplates, defrules and facts into a file, which is
 * compatible to the format expected by the batch function. Returns true iff
 * everything could be dumped.
 */
public class Dump extends AbstractFunction {

	public static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Writes engine's deftemplates, defrules and facts into a file, which is compatible to the format expected by the batch function. Returns true iff everything could be dumped.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			if (parameter == 0)
				return "Output-file where to write the dump of the module.";
			if (parameter == 1)
				return "Module to be safed.";
			return null;
		}

		public String getParameterName(int parameter) {
			if (parameter == 0)
				return "output-file";
			if (parameter == 1)
				return "module";
			return null;
		}

		public JamochaType[] getParameterTypes(int parameter) {
			if (parameter <= 1)
				return JamochaType.STRINGS;
			return null;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return (parameter == 1);
		}

		public String getExample() {
			return "(dump /tmp/jamochadump.clp)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "dump";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public boolean dumpModule(Rete engine, String modName, BufferedWriter out,
			Formatter frm) {

		try {

			Module mod = engine.findModule(modName);

			// Map additionalInformation = new HashMap();

			out.write("%	Dump of module " + modName + "\n");

			// dumping templates
			out.write("\n\n%		Template definitions\n");
			for (Object t : mod.getTemplates()) {
				Template tmpl = (Template) t;
				if (!(tmpl.getName().equals("_initialFact")))
					out.write(tmpl.getDump(modName) + "\n");
			}

			// dumping facts
			// TODO argh it would be good, if module can give a list of all
			// facts. This solution is _horrible_!!
			out.write("\n\n%		Fact definitions\n");
			for (Object fObj : engine.getModules().getAllFacts()) {
				Fact fact = (Fact) fObj;
				if (mod.containsTemplate(fact.getTemplate())) {
					if (!(fact.getTemplate().getName().equals("_initialFact")))
						out.write(fact.getDump(modName) + "\n");
				}
			}

			// dumping rules
			out.write("\n\n%		Rule definitions\n");
			for (Object t : mod.getAllRules()) {
				Rule rule = (Rule) t;
				out.write(rule.format(frm));
			}

		} catch (IOException e) {
			return false;
		}

		return true;

	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		String inputfileName = params[0].getValue(engine).getStringValue();
		String modName = null;
		if (params.length > 1)
			modName = params[1].getValue(engine).getStringValue();
		BufferedWriter out;
		Formatter formatter = ParserFactory.getFormatter();
		try {
			out = new BufferedWriter(new FileWriter(inputfileName));
			out.write("% Jamocha Dump.\n\n");

			if (modName != null) {
				dumpModule(engine, modName, out, formatter);
			} else {
				for (Module mod : engine.getModules().getModuleList()) {
					dumpModule(engine, mod.getModuleName(), out, formatter);
				}
			}

			out.close();
		} catch (IOException e) {
			return JamochaValue.FALSE;
		}

		return JamochaValue.TRUE;
	}

}
