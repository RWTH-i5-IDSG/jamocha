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

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * ListDirectory will print out the files and folders in a given
 * directory. It's the same as dir in DOS and ls in unix.
 * @author pete
 *
 */
public class ListDirectoryFunction implements Function, Serializable {

	public static final String LIST_DIR = "list-dir";

	public ListDirectoryFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		if (params != null && params.length > 0) {
			File dir = new File(params[0].getStringValue());
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (int idx=0; idx < files.length; idx++) {
					if (files[idx].isDirectory()) {
						engine.writeMessage("d " + files[idx] + Constants.LINEBREAK);
					} else {
						engine.writeMessage("- " + files[idx] + Constants.LINEBREAK);
					}
				}
				engine.writeMessage(files.length + " files in the directory" + 
						Constants.LINEBREAK, "t");
			} else {
				
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return LIST_DIR;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	/**
	 * for now, just return the simple form. need to implement the method
	 * completely.
	 */
	public String toPPString(Parameter[] params, int indents) {
		if (indents > 0) {
			StringBuffer buf = new StringBuffer();
			for (int idx = 0; idx < indents; idx++) {
				buf.append(" ");
			}
			buf.append("(list-dir)");
			return buf.toString();
		} else {
			return "(list-dir)";
		}
	}

}
