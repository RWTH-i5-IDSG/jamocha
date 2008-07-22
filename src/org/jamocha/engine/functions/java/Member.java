/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.functions.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Christian Ebert
 * 
 * Calls a method of a specified object. Returns the resulting value of the
 * method or false.
 */
public class Member extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Calls a method of a specified object. Returns the resulting value of the method or false.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Object to call the method of.";
			case 1:
				return "Method to call.";
			}
			return "One or more parameters for the method.";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "object";
			case 1:
				return "method";
			}
			return "parameter";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.OBJECTS;
			case 1:
				return JamochaType.STRINGS;
			}
			return JamochaType.OBJECTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.ANY;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return parameter > 1;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
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

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "member";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		Object o = null;
		String methodname = null;
		Object[] args = null;
		if (params != null && params.length >= 2) {
			o = params[0].getValue(engine).getObjectValue();
			methodname = params[1].getValue(engine).getIdentifierValue();
			args = new Object[params.length - 2];
			for (int idx = 2; idx < params.length; idx++)
				args[idx - 2] = params[idx].getValue(engine).getObjectValue();
			try {
				Class classDefinition = o.getClass();
				Method[] methods = classDefinition.getMethods();
				for (int i = 0; i < methods.length; ++i) {
					Method method = methods[i];
					if (method.getName().equals(methodname)) {
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length == args.length) {
							boolean compatible = true;
							for (int j = 0; j < args.length && compatible; ++j)
								compatible &= parameterTypes[j]
										.isInstance(args[j]);
							if (compatible)
								return JamochaValue.newObject(method.invoke(o,
										args));
						}
					}
				}
			} catch (IllegalAccessException e) {
				throw new EvaluationException(e);
			} catch (SecurityException e) {
				throw new EvaluationException(e);
			} catch (IllegalArgumentException e) {
				throw new EvaluationException(e);
			} catch (InvocationTargetException e) {
				throw new EvaluationException(e);
			}
		}
		return JamochaValue.FALSE;
	}
}