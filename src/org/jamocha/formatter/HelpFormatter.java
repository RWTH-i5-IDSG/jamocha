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

package org.jamocha.formatter;

import java.util.List;

import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.JamochaType;

public class HelpFormatter extends SFPFormatter {

	public String visit(Function object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(object.getName());
		List<String> aliases = object.getAliases();
		for (String alias : aliases) {
			sb.append(" | ").append(alias);
		}
		FunctionDescription desc = object.getDescription();
		increaseIndent();
		for (int i = 0; i < desc.getParameterCount(); ++i) {
			newLine(sb);
			if (desc.isParameterOptional(i))
				sb.append("[");
			else
				sb.append("(");
			sb.append(desc.getParameterName(i));
			if (desc.isParameterOptional(i))
				sb.append("]");
			else
				sb.append(")");
			sb.append(" <");
			JamochaType[] types = desc.getParameterTypes(i);
			for (int j = 0; j < types.length; ++j) {
				if (j > 0)
					sb.append(" | ");
				sb.append(types[j]);
			}
			sb.append(">");
			increaseIndent();
			newLine(sb);
			sb.append(desc.getParameterDescription(i));
			decreaseIndent();
		}
		decreaseIndent();
		newLine(sb);
		sb.append(")");
		if (!desc.isParameterCountFixed()) {
			newLine(sb);
			sb.append("(The parameter count for this function is variable)");
		}
		newLine(sb);
		newLine(sb);
		sb.append("returns: <").append(
				formatParameterTypes(desc.getReturnType())).append(">");
		newLine(sb);
		newLine(sb);
		sb.append(desc.getDescription());
		String example = desc.getExample();
		if (example != null) {
			newLine(sb);
			newLine(sb);
			sb.append("Example(s):");
			newLine(sb);
			sb.append(example);
		}
		return sb.toString();
	}

	public String visit(Fact object) {
		StringBuilder sb = new StringBuilder();
		sb.append("f-").append(object.getFactId());
		sb.append(super.visit(object));
		return sb.toString();
	}

	private String formatParameterTypes(JamochaType[] types) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < types.length; ++j) {
			if (j > 0)
				sb.append("|");
			sb.append(types[j]);
		}
		return sb.toString();
	}

}
