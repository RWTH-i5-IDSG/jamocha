package org.jamocha.rete;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.formatter.Formattable;
import org.jamocha.parser.ParserFactory;

public abstract class ExpressionCollection implements Parameter, Formattable {

	protected ArrayList<Parameter> parameterList = new ArrayList<Parameter>();

	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException(); // abstract class!
	}
	
	public ExpressionCollection() {
		super();
	}

	public boolean isObjectBinding() {
		boolean objectBinding = false;
		for (int i = 0; i < parameterList.size() && !objectBinding; ++i) {
			objectBinding |= parameterList.get(i).isObjectBinding();
		}
		return objectBinding;
	}

	public boolean add(Parameter o) {
		return parameterList.add(o);
	}

	public Parameter get(int index) {
		return parameterList.get(index);
	}
	
	public void toArray(Parameter[] params){
	    parameterList.toArray(params);
	}
	
	public List<Parameter> getList(){
		return parameterList;
	}
	

	public int size() {
		return parameterList.size();
	}

	public String getExpressionString() {
		return ParserFactory.getFormatter().visit(this);
	}
	
}