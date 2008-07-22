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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;

/**
 * @author Peter Lin
 * 
 * Tests whether an object given as first argument is of a certain type,
 * specified in the second argument. Returns true if this is the case, false
 * otherwise.
 */
public class Instanceof extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Tests whether an object given as first argument is of a certain type, "
					+ "specified in the second argument. Returns true if this is the case, false otherwise.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Object to test.";
			case 1:
				return "Name of the class to test for.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "object";
			case 1:
				return "class";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.OBJECTS;
			case 1:
				return JamochaType.STRINGS;
			}
			return JamochaType.NONE;
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

		public Object getExpectedResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "instanceof";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private final ClassnameResolver classnameResolver;

	public Instanceof(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length == 2) {
			Object object = params[0].getValue(engine).getObjectValue();
			String className = params[1].getValue(engine).getIdentifierValue();
			try {
				Class<?> clazz = classnameResolver.resolveClass(className);
				if (clazz.isInstance(object))
					result = JamochaValue.TRUE;
			} catch (ClassNotFoundException e) {
				throw new EvaluationException(e);
			}
		} else
			throw new IllegalParameterException(2);
		return result;
	}

}
