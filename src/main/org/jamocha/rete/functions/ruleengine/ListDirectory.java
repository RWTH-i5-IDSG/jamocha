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

import java.io.File;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Prints out the files and folders of a given directory. It is the same 
 * command as dir in DOS and ls in Unix. 
 * The return value is NIL.";
 */
public class ListDirectory extends Function {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Prints out the files and folders of a given directory. It is the same command as" +
					"dir in DOS and ls in Unix. The return value is NIL.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Directory to list files and folders of.";
		}

		public String getParameterName(int parameter) {
			return "dir";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.NONE;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			return "(list-dir examples)\n" +
					"(list-dir /var/tmp)";
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "list-dir";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		if (params != null && params.length > 0) {
			File dir = new File(params[0].getValue(engine).getStringValue());
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (int idx = 0; idx < files.length; idx++) {
					if (files[idx].isDirectory()) {
						engine.writeMessage("d " + files[idx]
								+ Constants.LINEBREAK);
					} else {
						engine.writeMessage("- " + files[idx]
								+ Constants.LINEBREAK);
					}
				}
				engine.writeMessage(files.length + " files in the directory"
						+ Constants.LINEBREAK);
			} else {
				engine.writeMessage(dir.getPath() + " is not a directory.");
			}
		}
		return JamochaValue.NIL;
	}
}
