package org.jamocha.rete.functions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Christian Ebert
 * 
 * Calls a method of a specified object.
 */


public class MemberFunction implements Function, Serializable {
	
	public static final String MEMBER = "member";
	
	public MemberFunction(ClassnameResolver classnameResolver){
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Object o = null;
		Object ro = null;
		String methodname = null;
		Class [] argsclass = null;
		Object [] args = null;
		if (params != null) {
			if (params[0] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[0];
				o = n.getValue();
			}
			else if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				o = engine.getBinding(bp.getVariableName());
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				o = rval.firstReturnValue().getValue();
			}
			if (params[1] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[1];
				methodname = n.getStringValue();
			}
			else if (params[1] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[1];
				methodname = (String) engine.getBinding(bp
						.getVariableName());
			} else if (params[1] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[1];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				methodname = rval.firstReturnValue().getStringValue();
			}
			if(params.length > 2){
				argsclass = new Class[params.length-1];
				args = new Object[params.length-1];
			}
			for (int idx = 2; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[idx];
					argsclass[idx-1] = n.getValue().getClass();
					args[idx-1] = n.getValue();
				} else if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					argsclass[idx-1] = engine.getBinding(bp.getVariableName()).getClass();
					args[idx-1] = engine.getBinding(bp.getVariableName());
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[idx];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					argsclass[idx-1] = rval.firstReturnValue().getValue().getClass();
					args[idx-1] = rval.firstReturnValue().getValue();
				}
			} 
			try {
				Class classDefinition = o.getClass();
				Method method = classDefinition.getMethod(methodname, argsclass);
				ro = method.invoke(o, args);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
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
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE,
				ro);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return MEMBER;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(member");
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
			return "(member (<literal> | <binding> | <value>)+)\n";
		}
	}

}
