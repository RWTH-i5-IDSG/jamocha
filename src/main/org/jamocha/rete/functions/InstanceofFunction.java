package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;

public class InstanceofFunction implements Function, Serializable {
	
	public static final String INSTANCEOF = "instanceof";
	
	private ClassnameResolver classnameResolver;
	
	public InstanceofFunction(ClassnameResolver classnameResolver){
		super();
		this.classnameResolver = classnameResolver;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		boolean eval = false;
		if (params.length == 2) {
			Object param1 = null;
			if (params[0] instanceof BoundParam && params[1] instanceof BoundParam) {
				param1 = ((BoundParam) params[0]).getObjectRef();
				try {
					Class<?> clazz = classnameResolver.resolveClass(((BoundParam) params[1]).getStringValue());
					eval = clazz.isInstance(param1);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			} 
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(eval));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return INSTANCEOF;
	}

	public Class[] getParameter() {
		return new Class[] {BoundParam.class,BoundParam.class};
	}

	public JamochaType getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(instanceof");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else {
					buf.append(" " + params[idx].getStringValue());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(instanceof <Java-object> <class-name>)\n"; 
		}
	}

}
