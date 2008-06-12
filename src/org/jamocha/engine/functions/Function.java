/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import java.util.List;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;

public interface Function {

	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException;

	public FunctionDescription getDescription();

	public String getName();

	public void addToFunctionGroup(FunctionGroup group);

	public void removeFromFunctionGroup(FunctionGroup group);

	public List<FunctionGroup> getFunctionGroups();

	public List<String> getAliases();

	public String format(Formatter visitor);

}