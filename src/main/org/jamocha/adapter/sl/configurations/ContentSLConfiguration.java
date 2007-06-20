package org.jamocha.adapter.sl.configurations;

import java.util.LinkedList;
import java.util.List;

public class ContentSLConfiguration implements SLConfiguration {

	private List<SLConfiguration> expressions = new LinkedList<SLConfiguration>();

	public void addExpression(SLConfiguration expression) {
		expressions.add(expression);
	}

	public List<SLConfiguration> getExpressions() {
		return expressions;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		for(SLConfiguration expression : expressions) {
			res.append(expression.compile(compileType));
		}
		return res.toString();
	}
}
