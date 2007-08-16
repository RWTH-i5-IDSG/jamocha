package org.jamocha.formatter;

import java.util.List;

import org.jamocha.parser.JamochaType;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

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
