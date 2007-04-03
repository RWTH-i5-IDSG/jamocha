package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

/**
 * @author Christian Ebert
 * 
 * Creates a Java Object and returns it.
 */

public class LoadPackageFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NAME = "load-package";

	private ClassnameResolver classnameResolver;

	public LoadPackageFunction(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

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
					engine.declareFunctionGroup((FunctionGroup) o);
					result = JamochaValue.TRUE;
				}
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
			}
		}
		return result;
	}

	public String getName() {
		return NAME;
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(load-package");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" ").append(params[idx].getExpressionString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(load-package <classname>)\n";
		}
	}

}
