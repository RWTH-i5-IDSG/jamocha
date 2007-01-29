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
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Christian Ebert
 * 
 * Creates a Java Object and returns it.
 */

public class LoadPackageFunction implements Function, Serializable {

	public static final String FUNCTION_NAME = "load-package";

	private ClassnameResolver classnameResolver;

	public LoadPackageFunction(ClassnameResolver classnameResolver) {
		super();
		this.classnameResolver = classnameResolver;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		Object o = null;
		String classname = null;
		if (params != null && params.length == 1) {
			if (params[0] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[0];
				classname = n.getStringValue();
			} else if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				classname = (String) engine.getBinding(bp.getVariableName());
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				classname = rval.firstReturnValue().getStringValue();
			}
			try {
				Class classDefinition = classnameResolver
						.resolveClass(classname);
				o = classDefinition.newInstance();
				if(o instanceof FunctionGroup) {
					engine.declareFunctionGroup((FunctionGroup) o);
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
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE, o);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return FUNCTION_NAME;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public JamochaType getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(load-package");
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
			return "(load-package <classname>)\n";
		}
	}

}
