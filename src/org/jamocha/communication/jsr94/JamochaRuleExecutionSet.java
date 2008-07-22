/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.communication.jsr94;

import java.util.ArrayList;
import java.util.List;

import javax.rules.admin.RuleExecutionSet;

import org.jamocha.parser.Expression;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleExecutionSet implements RuleExecutionSet {

	private static final long serialVersionUID = 1L;

	private final String description;

	private final String name;

	private String defaultFilter;

	private final Expression[] exprs;

	public JamochaRuleExecutionSet(String description, String name,
			Expression[] exprs) {
		this.description = description;
		this.name = name;
		this.exprs = exprs;
	}

	public String getDefaultObjectFilter() {
		return defaultFilter;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public Object getProperty(Object arg0) {
		// TODO do something useful with them
		return null;
	}

	@SuppressWarnings("unchecked")
	public List getRules() {
		List result = new ArrayList(exprs.length);
		for (Expression r : exprs)
			result.add(r);
		return result;
	}

	public void setDefaultObjectFilter(String defFilter) {
		defaultFilter = defFilter;
	}

	public void setProperty(Object arg0, Object arg1) {
		// TODO do something useful with them
	}

	public Expression[] getExpressions() {
		return exprs;
	}

}
