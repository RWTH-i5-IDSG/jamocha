package org.jamocha.rete;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpressionCollection implements Parameter{

    protected List<Parameter> parameterList = new ArrayList<Parameter>();

    public ExpressionCollection() {
	super();
    }

    public boolean isObjectBinding() {
        boolean objectBinding = false;
        for(int i=0; i<parameterList.size() && !objectBinding; ++i) {
            objectBinding |= parameterList.get(i).isObjectBinding();
        }
        return objectBinding;
    }

    public boolean add(Parameter o) {
        return parameterList.add(o);
    }

    public String getExpressionString() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<parameterList.size(); ++i) {
            sb.append(parameterList.get(i).getExpressionString());
        }
        return sb.toString();
    }

}