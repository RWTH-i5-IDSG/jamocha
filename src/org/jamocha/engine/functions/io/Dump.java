/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.functions.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.modules.Module;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn
 * 
 * Writes engine's deftemplates, defrules and facts into a file, which is
 * compatible to the format expected by the batch function. Returns true iff
 * everything could be dumped.
 */
public class Dump extends AbstractFunction {

	private static final class Description implements FunctionDescription {

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
			return parameter == 1;
		}

		public String getExample() {
			return "(dump /tmp/jamochadump.clp)";
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = 1L;

	public static final FunctionDescription DESCRIPTION = new Description();

	public static final String NAME = "dump";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		String inputfileName = params[0].getValue(engine).getStringValue();
		String modName = null;
		if (params.length > 1)
			modName = params[1].getValue(engine).getStringValue();

		BufferedWriter out;

		try {
			out = new BufferedWriter(new FileWriter(inputfileName));
			out.write("% Jamocha Dump.\n\n");
			if (modName == null)
				out.write(engine.getDump());
			else {
				Module mod = engine.findModule(modName);
				// TODO: dump facts somewhere here!!
				if (mod == null)
					return JamochaValue.FALSE;
				out.write(mod.getDump());
			}
			out.close();
		} catch (IOException e) {
			Logging.logger(this.getClass()).warn(e);
			return JamochaValue.FALSE;
		}
		return JamochaValue.TRUE;
	}

}
