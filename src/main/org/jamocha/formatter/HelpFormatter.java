package org.jamocha.formatter;

import org.jamocha.parser.JamochaType;
import org.jamocha.rete.AbstractFunction;
import org.jamocha.rete.functions.FunctionDescription;

public class HelpFormatter extends SFPFormatter {

	public String visit(AbstractFunction object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(object.getName());
		FunctionDescription desc = object.getDescription();
		increaseIndent();
		for (int i = 0; i < desc.getParameterCount(); ++i) {
			newLine(sb);
			sb.append("?").append(desc.getParameterName(i));
			sb.append(" <");
			JamochaType[] types = desc.getParameterTypes(i);
			for (int j = 0; j < types.length; ++j) {
				if (j > 0)
					sb.append("|");
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
		return sb.toString();
	}
	
}
