package org.jamocha.rete.functions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Christian Ebert
 * 
 * Calls a method of a specified object.
 */

public class MemberFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "member";

	public MemberFunction(ClassnameResolver classnameResolver) {
		super();
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Object o = null;
		String methodname = null;
		Object [] args = null;
		if (params != null && params.length >= 2) {
			o = params[0].getValue(engine).getObjectValue();
			methodname = params[1].getValue(engine).getIdentifierValue();
				args = new Object[params.length-2];
			for (int idx = 2; idx < params.length; idx++) {
				args[idx-2] = params[idx].getValue(engine).getObjectValue();
			} 
			try {
				Class classDefinition = o.getClass();
				Method[] methods = classDefinition.getMethods(); 
				for(int i=0;i<methods.length; ++i) {
					Method method = methods[i];
					if(method.getName().equals(methodname)) {
						Class<?>[] parameterTypes = method.getParameterTypes(); 
						if(parameterTypes.length == args.length) {
							boolean compatible = true;
							for(int j=0; j<args.length && compatible; ++j) {
								compatible &= parameterTypes[j].isInstance(args[j]);
							}
							if(compatible) {
								return JamochaValue.newObject(method.invoke(o, args));
							}
						}
					}
				}
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
		return JamochaValue.FALSE;
	}

	public String getName() {
		return NAME;
	}

	public JamochaType getReturnType() {
		return JamochaType.OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(member");
			for (int idx = 0; idx < params.length; idx++) {
					buf.append(" ").append(params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(member (<literal> | <binding> | <value>)+)\n";
		}
	}

}
