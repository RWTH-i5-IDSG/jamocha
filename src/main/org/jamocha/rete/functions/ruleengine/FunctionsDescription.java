/*
 * Copyright 2007 Alexander Wilden
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Josef Alexander Hahn
 * 
 * This function gets an XML-document, which describes the declared functions
 */
public class FunctionsDescription implements Function, Serializable {

	private static final class Description implements
			FunctionDescription {

		public String getDescription() {
			return "This function gets an XML-document, which describes the declared functions.";
		}

		public int getParameterCount() {
			return 0;
		}

		public String getParameterDescription(int parameter) {
			return "";
		}

		public String getParameterName(int parameter) {
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}
	
	private class XmlTag {
		private String name;
		private HashMap<String, String> attributes;
		private ArrayList<XmlTag> childs;
		
		public XmlTag() {
			attributes = new HashMap<String, String>();
			childs=new ArrayList<XmlTag>();
		}
		
		String getName() {return name;}
		void setName(String name) {this.name=name;}
		void addAttribute(String name, String value) {attributes.put(name, value);}
		void clearAttributes() {attributes.clear();}
		void clearChilds() {childs.clear();}
		void addChild(XmlTag tag) {childs.add(tag);}
		
		public void appendToStringBuilder(StringBuilder sb) {
			sb.append("<");
			sb.append(name);
			Iterator<String> it=attributes.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				String value= attributes.get(name);
				sb.append(" ");
				sb.append(name);
				sb.append("=\"");
				sb.append(value.replace('"', '\'' ));
				sb.append("\"");
			}
			if (childs.isEmpty()) {
				sb.append("/>");
			} else {
				sb.append(">");
				Iterator<XmlTag> itChilds=childs.iterator();
				while (itChilds.hasNext()){
					XmlTag child=itChilds.next();
					child.appendToStringBuilder(sb);
				}
				sb.append("</");
				sb.append(name);
				sb.append(">");
			}
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "functions-description";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	private String JamochaType2String(JamochaType[] t) {
		if (t==JamochaType.BOOLEANS) return "boolean";
		if (t==JamochaType.DATETIMES) return "datetime";
		if (t==JamochaType.DOUBLES) return "double";
		if (t==JamochaType.FACT_IDS) return "fact-id";
		if (t==JamochaType.FACTS) return "facts";
		if (t==JamochaType.IDENTIFIERS) return "identifier";
		if (t==JamochaType.LISTS) return "list";
		if (t==JamochaType.LONGS) return "long";
		if (t==JamochaType.NONE) return "none";
		if (t==JamochaType.NUMBERS) return "number";
		if (t==JamochaType.OBJECTS) return "java-object";
		if (t==JamochaType.PRIMITIVES) return "primitive";
		if (t==JamochaType.SLOTS) return "slot";
		if (t==JamochaType.STRINGS) return "string";
		return "unknown";
	}
	
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {

		StringBuilder xmlDocument = new StringBuilder();
		xmlDocument.append(" <?xml version=\"1.0\" encoding=\"utf-8\" ?> ");
		xmlDocument.append("<functiongroups>");
		Iterator itGroup=engine.getFunctionGroups().iterator() ;
		while( itGroup.hasNext() ){
			XmlTag groupTag = new XmlTag();
			FunctionGroup group = (FunctionGroup)itGroup.next();
			groupTag.setName("functiongroup");
			groupTag.addAttribute("name", group.getName());
			
			Iterator itFunc=group.listFunctions().iterator();
			while (itFunc.hasNext()) {
				
				Function function=(Function)itFunc.next();
				FunctionDescription desc=function.getDescription();
		
				XmlTag t=new XmlTag();
				t.setName("function");
				t.addAttribute("name", function.getName());
				t.addAttribute("description", desc.getDescription());
				
				if (desc.isParameterCountFixed()) {
					t.addAttribute("fixedParameterCount", "true");
				} else {
					t.addAttribute("fixedParameterCount", "false");
				}
				
				t.addAttribute("returnType", JamochaType2String(desc.getReturnType()));
				
				for( int i=0 ; i<desc.getParameterCount() ; i++) {
					XmlTag param=new XmlTag();
					param.setName("parameter");
					param.addAttribute("description", desc.getParameterDescription(i));
					param.addAttribute("type", JamochaType2String(desc.getParameterTypes(i) ));
					if (desc.isParameterOptional(i)) {
						param.addAttribute("optional", "true");
					} else {
						param.addAttribute("optional", "false");
					}
					t.addChild(param);
				}
				groupTag.addChild(t);
			}
			groupTag.appendToStringBuilder(xmlDocument);
		}
		xmlDocument.append("</functiongroups>");
		
		return JamochaValue.newString(xmlDocument.toString());
	}
}
