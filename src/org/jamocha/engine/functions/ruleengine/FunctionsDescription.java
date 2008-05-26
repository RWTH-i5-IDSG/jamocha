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

package org.jamocha.engine.functions.ruleengine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.languages.clips.parser.SFPParser;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn
 * 
 * This function gets an XML-document, which describes the declared functions
 */
public class FunctionsDescription extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "This function generates a XML-document, which describes the declared functions. If a filename is given, it only prints that to file and returns an empty string.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			if (parameter == 1)
				return "destination file name";
			return "";
		}

		public String getParameterName(int parameter) {
			if (parameter == 1)
				return "filename";
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			if (parameter == 1)
				return JamochaType.STRINGS;
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			if (parameter == 1)
				return true;
			return false;

		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// NEVER set this to true! it would lead to an infinite loop!
			return false;
		}

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private class XmlTag {
		private String name;

		private final HashMap<String, String> attributes;

		private final ArrayList<XmlTag> childs;

		private String body;

		public XmlTag() {
			attributes = new HashMap<String, String>();
			childs = new ArrayList<XmlTag>();
			body = "";
		}

		String getName() {
			return name;
		}

		void setName(String name) {
			this.name = name;
		}

		void setBody(String body) {
			this.body = body;
		}

		void addAttribute(String name, String value) {
			if (value != null)
				attributes.put(name, value);
		}

		void clearAttributes() {
			attributes.clear();
		}

		void clearChilds() {
			childs.clear();
		}

		void addChild(XmlTag tag) {
			childs.add(tag);
		}

		public void appendToStringBuilder(StringBuilder sb) {
			sb.append("<");
			sb.append(name);
			Iterator<String> it = attributes.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				String value = attributes.get(name);
				sb.append(" ");
				sb.append(name);
				sb.append("=\"");
				sb.append(value.replace('"', '\'').replace(">", "&gt;")
						.replace("<", "&lt;"));
				sb.append("\"");
			}
			if (childs.isEmpty() && body.length() == 0)
				sb.append("/>");
			else {
				sb.append(">");
				Iterator<XmlTag> itChilds = childs.iterator();
				while (itChilds.hasNext()) {
					XmlTag child = itChilds.next();
					child.appendToStringBuilder(sb);
				}
				sb.append(body.replace('"', '\'').replace(">", "&gt;").replace(
						"<", "&lt;"));
				sb.append("</");
				sb.append(name);
				sb.append(">");
			}
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "functions-description";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private String JamochaType2String(JamochaType[] t) {
		if (t == JamochaType.BOOLEANS)
			return "BOOLEAN";
		if (t == JamochaType.DATETIMES)
			return "DATETIME";
		if (t == JamochaType.DOUBLES)
			return "DOUBLE";
		if (t == JamochaType.FACT_IDS)
			return "FACT_ID";
		if (t == JamochaType.FACTS)
			return "FACT";
		if (t == JamochaType.IDENTIFIERS)
			return "IDENTIFIER";
		if (t == JamochaType.LISTS)
			return "LIST";
		if (t == JamochaType.LONGS)
			return "LONG";
		if (t == JamochaType.NONE)
			return "NONE";
		if (t == JamochaType.NUMBERS)
			return "NUMBER";
		if (t == JamochaType.OBJECTS)
			return "OBJECT";
		if (t == JamochaType.PRIMITIVES)
			return "PRIMITIVE";
		if (t == JamochaType.SLOTS)
			return "SLOT";
		if (t == JamochaType.STRINGS)
			return "STRING";
		return "unknown";
	}

	private String execute(String clipsCode) {
		StringBuilder result = new StringBuilder();

		Engine engine = new Engine();

		int next = 0;
		int ind = 0;

		try {
			while (next < clipsCode.length()) {

				ind = next + 1;
				int opened = 0;

				if (ind >= clipsCode.length())
					break;

				while (clipsCode.charAt(ind) != ')' || opened > 0) {
					if (clipsCode.charAt(ind) == ')')
						opened--;
					if (clipsCode.charAt(ind) == '(')
						opened++;
					ind++;
				}
				String expression = clipsCode.substring(next, ind + 1);
				next = ind + 1;
				while (next < clipsCode.length() - 1
						&& clipsCode.charAt(next) != '(')
					next++;

				Expression expr = new SFPParser(new StringReader(expression))
						.nextExpression();

				result.append(Constants.SHELL_PROMPT);
				result.append(expression + "\n");
				result.append(expr.getValue(engine).toString()).append("\n");
			}
		} catch (Exception e) {
			engine.clearAll();
			System.err
					.println("Warning: While executing a documentation example, an exception was thrown. Clips code was:\n"
							+ clipsCode + "\n\nExceptios:\n");
			e.printStackTrace(System.err);
			return clipsCode;
		}
		engine.clearAll();
		return result.toString();
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {

		StringBuilder xmlDocument = new StringBuilder();
		xmlDocument.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?> ");
		xmlDocument.append("<functiongroups>");
		Iterator<FunctionGroup> itGroup = engine.getFunctionMemory()
				.getFunctionGroups().values().iterator();
		while (itGroup.hasNext()) {
			XmlTag groupTag = new XmlTag();
			FunctionGroup group = itGroup.next();
			groupTag.setName("functiongroup");
			groupTag.addAttribute("name", group.getName());

			Iterator<Function> itFunc = group.listFunctions().iterator();
			while (itFunc.hasNext()) {

				Function function = itFunc.next();
				FunctionDescription desc = function.getDescription();

				XmlTag t = new XmlTag();
				t.setName("function");
				t.addAttribute("name", function.getName());
				if (desc == null)
					continue;
				t.addAttribute("description", desc.getDescription());

				if (desc.isParameterCountFixed())
					t.addAttribute("fixedParameterCount", "true");
				else
					t.addAttribute("fixedParameterCount", "false");

				// example

				String ex = desc.getExample();
				if (ex != null) {

					// / maybe we may generate results
					if (desc.isResultAutoGeneratable())
						ex = execute(ex);

					XmlTag example = new XmlTag();
					example.setName("example");
					example.setBody(ex);
					t.addChild(example);
				}

				t.addAttribute("returnType", JamochaType2String(desc
						.getReturnType()));

				for (int i = 0; i < desc.getParameterCount(); i++) {
					XmlTag param = new XmlTag();
					param.setName("parameter");
					param.addAttribute("name", desc.getParameterName(i));
					param.addAttribute("description", desc
							.getParameterDescription(i));
					param.addAttribute("type", JamochaType2String(desc
							.getParameterTypes(i)));
					if (desc.isParameterOptional(i))
						param.addAttribute("optional", "true");
					else
						param.addAttribute("optional", "false");
					t.addChild(param);
				}
				groupTag.addChild(t);
			}
			groupTag.appendToStringBuilder(xmlDocument);
		}
		xmlDocument.append("</functiongroups>");

		String res = xmlDocument.toString();

		if (params.length > 0) {
			String file = params[0].getValue(engine).toString();
			FileWriter fw;
			try {
				fw = new FileWriter(file);
				fw.write(res);
				fw.close();
				return JamochaValue.newString("");
			} catch (IOException e) {
				engine.writeMessage(e.getMessage());
			}

		}
		return JamochaValue.newString(res);

	}
}
