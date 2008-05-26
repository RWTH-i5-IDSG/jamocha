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

package org.jamocha.engine;

import java.util.ArrayList;

import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

public class ExpressionSequence extends ExpressionCollection implements
		Formattable {

	@Override
	public Object clone() {
		@SuppressWarnings("unchecked")
		ArrayList<Parameter> paramList = (ArrayList<Parameter>) parameterList
				.clone();
		ExpressionCollection result = new ExpressionSequence();
		result.parameterList = paramList;
		return result;
	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		JamochaValue result = JamochaValue.NIL;
		Parameter param;
		for (int i = 0; i < parameterList.size(); ++i) {
			param = parameterList.get(i);
			try {
				result = param.getValue(engine);
			} catch (Exception e) {
				throw new EvaluationException("Error in: " + param.toString(),
						e);
			}
		}
		return result;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getExpressionString() {
		return ParserFactory.getFormatter().visit(this);
	}

}
