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

package org.jamocha.engine.functions;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public abstract class AbstractFunction implements Function {

	protected List<String> aliases = new ArrayList<String>();

	protected List<FunctionGroup> functionGroups = new ArrayList<FunctionGroup>(
			1);

	public AbstractFunction() {
	}

	public abstract JamochaValue executeFunction(Engine engine,	Parameter[] params) throws EvaluationException;

	public abstract FunctionDescription getDescription();

	public abstract String getName();

	@Override
	public String toString() {
		return "(" + getName() + ")";
	}

	public void addToFunctionGroup(FunctionGroup group) {
		if (!functionGroups.contains(group))
			functionGroups.add(group);
	}

	public void removeFromFunctionGroup(FunctionGroup group) {
		functionGroups.remove(group);
	}

	public List<FunctionGroup> getFunctionGroups() {
		return functionGroups;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
