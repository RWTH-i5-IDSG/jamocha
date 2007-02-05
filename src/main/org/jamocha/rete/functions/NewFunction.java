package org.jamocha.rete.functions;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

/**
 * @author Christian Ebert
 * 
 * Creates a Java Object and returns it.
 */

public class NewFunction implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NEW = "new";

	private ClassnameResolver classnameResolver;

	public NewFunction(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

	public JamochaType getReturnType() {
		return JamochaType.OBJECT;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		Object o = null;
		String classname = null;
		Object[] args = null;
		if (params != null) {
			if (params.length > 0) {
				classname = params[0].getValue(engine).getStringValue();
				args = new Object[params.length - 1];
				for (int idx = 1; idx < params.length; idx++) {
					args[idx - 1] = params[idx].getValue(engine)
							.getObjectValue();
				}
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
							for (int i = 0; i < parameterClasses.length; ++i) {
								match &= (parameterClasses[i]
										.isInstance(args[i]) || args[i] == null);
							}
							if (match) {
								foundConstructor = constructor;
								break;
							}
						}
					}
					if (foundConstructor != null) {
						o = foundConstructor.newInstance(args);
					}
					return JamochaValue.newObject(o);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		throw new IllegalParameterException(1, true);
	}

	public String getName() {
		return NEW;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(new");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getParameterString());
				} else {
					buf.append(" " + params[idx].getParameterString());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(new (<literal> | <binding> | <value>)+)\n";
		}
	}

}
