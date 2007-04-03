/* Generated By:JJTree: Do not edit this line. COOLDeffunctionConstruct.java */
package org.jamocha.parser.cool;

import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.functions.DeffunctionFunction;

public class COOLDeffunctionConstruct extends ConstructNode {

    private boolean varargs;

    private Parameter[] parameters;
    
    private Expression functionActions;

    public COOLDeffunctionConstruct(int id) {
	super(id);
    }

    public COOLDeffunctionConstruct(COOLParser p, int id) {
	super(p, id);
    }

    public void setName(String n) {
	name = n;
    }

    public void setDocString(String ds) {
	doc = ds;
    }

    public String toString() {
	return "deffunction \"" + name + " (" + doc + ")";
    }

    public void setFunctionActions(Expression n) {
	functionActions = n;
    };

    public void setFunctionParams(List<String> list) {
	parameters = new Parameter[list.size()];
	for (int i = 0; i < parameters.length; ++i) {
	    parameters[i] = new BoundParam(list.get(i));
	}
    }

    public void hasMultiVars(boolean has) {
	varargs = true;
    }

    public Parameter getExpression() // throws EvaluationException
    {
	Parameter[] params = new Parameter[3];
	params[0] = new ValueParam(JamochaValue.newIdentifier(name));
	// params[1] = new ValueParam(JamochaValue.newString(getDocString()));
	params[1] = new ValueParam(JamochaValue.newObject(parameters));
	params[2] = new ValueParam(JamochaValue.newObject(functionActions));
	FunctionParam2 functionParam = new FunctionParam2();
	functionParam.setParameters(params);
	functionParam.setFunctionName(DeffunctionFunction.NAME);
	return functionParam;
    };

}
