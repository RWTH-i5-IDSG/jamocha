package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public class InstanceofFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String INSTANCEOF = "instanceof";

	private ClassnameResolver classnameResolver;

	public InstanceofFunction(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params.length == 2) {
			Object object = params[0].getValue(engine).getObjectValue();
			String className = params[1].getValue(engine).getIdentifierValue();
			try {
				Class<?> clazz = classnameResolver.resolveClass(className);
				if (clazz.isInstance(object)) {
					result = JamochaValue.TRUE;
				}
			} catch (ClassNotFoundException e) {
				throw new EvaluationException(e);
			}
		} else {
			throw new IllegalParameterException(2);
		}
		return result;
	}

	public String getName() {
		return INSTANCEOF;
	}

	public Class[] getParameter() {
		return new Class[] { BoundParam.class, BoundParam.class };
	}

	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(instanceof");
			for (int idx = 0; idx < params.length; idx++) {
					buf.append(" ").append(params[idx].getParameterString());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(instanceof <Java-object> <class-name>)\n";
		}
	}

}
