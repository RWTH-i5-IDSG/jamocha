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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Christian Ebert
 * 
 * Creates a Java Object and returns it. Searches for the constructor that
 * accepts the appropriate number of parameters.
 */
public class New extends AbstractFunction {

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Creates a Java object and returns it. Searches for the constructor that accepts the "
					+ "appropriate number of parameters.";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Object to instantiate.";
			}
			return "One or more parameters for the constructor.";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "object";
			}
			return "parameter";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.OBJECTS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.OBJECTS;
		}

		public boolean isParameterCountFixed() {
			return false;
		}

		public boolean isParameterOptional(int parameter) {
			return parameter > 0;
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

	public static final String NAME = "new";

	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private final ClassnameResolver classnameResolver;

	public New(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		Object o = null;
		String classname = null;
		Object[] args = null;
		if (params != null)
			if (params.length > 0) {
				classname = params[0].getValue(engine).getStringValue();
				args = new Object[params.length - 1];
				for (int idx = 1; idx < params.length; idx++)
					args[idx - 1] = params[idx].getValue(engine)
							.getObjectValue();
				try {
					Class classDefinition = classnameResolver
							.resolveClass(classname);
					Constructor foundConstructor = null;
					for (Constructor constructor : classDefinition
							.getConstructors()) {
						Class[] parameterClasses = constructor
								.getParameterTypes();
						if (parameterClasses.length == args.length) {
							boolean match = true;
							for (int i = 0; i < parameterClasses.length; ++i)
								match &= parameterClasses[i]
										.isInstance(args[i])
										|| args[i] == null;
							if (match) {
								foundConstructor = constructor;
								break;
							}
						}
					}
					if (foundConstructor != null)
						o = foundConstructor.newInstance(args);
					return JamochaValue.newObject(o);
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
				} catch (InvocationTargetException e) {
					throw new EvaluationException(e);
				}
			}
		throw new IllegalParameterException(1, true);
	}
}
