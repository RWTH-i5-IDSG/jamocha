/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.java;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rete.functions.FunctionGroup;

/**
 * @author Christian Ebert
 * 
 * Creates a Java Object and returns true on success.
 */
public class LoadPackage extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Creates a Java object and returns true on success.";
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Classname of the object to create.";
		}

		public String getParameterName(int parameter) {
			return "className";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.STRINGS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isResultAutoGeneratable() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "load-package";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	private ClassnameResolver classnameResolver;

	public LoadPackage(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

	@SuppressWarnings("unchecked")
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		Object o = null;
		String classname = null;
		if (params != null && params.length == 1) {
			classname = params[0].getValue(engine).getIdentifierValue();
			try {
				Class classDefinition = classnameResolver
						.resolveClass(classname);
				o = classDefinition.newInstance();
				if (o instanceof FunctionGroup) {
					engine.getFunctionMemory().declareFunctionGroup((FunctionGroup) o);
					result = JamochaValue.TRUE;
				}
			} catch (ClassNotFoundException e) {
				throw new EvaluationException(e);
			} catch (InstantiationException e) {
				throw new EvaluationException(e);
			} catch (IllegalAccessException e) {
				throw new EvaluationException(e);
			} catch (SecurityException e) {
				throw new EvaluationException(e);
			} catch (IllegalArgumentException e) {
				throw new EvaluationException(e);
			}
		}
		return result;
	}

}