package org.jamocha.formatter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.Formatter;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.DeffunctionConfiguration;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rule.Action;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndConnectedConstraint;
import org.jamocha.rule.ConditionWithNested;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.NotCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrConnectedConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

/*
 * 
 * New approach is to generate the clips string inside the
 * modeling objects, not centralized in this formatter.
 * 
 * for transition time, we will use this CLIPSFormatter (Backup exists)
 * which contains the old code for objects, which cannot generate
 * own clips strings yet
 */

public class CLIPSFormatter implements Formatter {

	private static final int INDENT_WIDTH = 4;

	private boolean indentation = true;

	private StringBuilder prefix = new StringBuilder();

	public CLIPSFormatter() {
	}

	public CLIPSFormatter(boolean indentation) {
		this.indentation = indentation;
	}

	public String formatExpression(Expression expression) {
		return expression.toClipsFormat(0);
	}

	public String formatFunction(Function function) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(function.getName());
		FunctionDescription fd = function.getDescription();
		int paramCount = fd.getParameterCount();
		increaseIndent();
		for (int i = 0; i < paramCount; ++i) {
			newLine(sb);
			if (fd.isParameterOptional(i))
				sb.append("[");
			else
				sb.append("(");
			sb.append(fd.getParameterName(i));
			if (fd.isParameterOptional(i))
				sb.append("]");
			else
				sb.append(")");
			sb.append(" <").append(
					formatParameterTypes(fd.getParameterTypes(i))).append(">");
			increaseIndent();
			newLine(sb);
			sb.append(fd.getParameterDescription(i));
			decreaseIndent();
		}
		decreaseIndent();
		newLine(sb);
		sb.append(")");
		newLine(sb);
		sb.append("returns: <")
				.append(formatParameterTypes(fd.getReturnType())).append(">");
		newLine(sb);
		newLine(sb);
		sb.append(fd.getDescription());
		String example = fd.getExample();
		if (example != null) {
			newLine(sb);
			newLine(sb);
			sb.append("Example(s):");
			newLine(sb);
			sb.append(example);
		}
		return sb.toString();
	}

	public String formatRule(Rule rule) {
		return rule.toClipsFormat(0);
	}

	private String formatParameterTypes(JamochaType[] types) {
		StringBuilder res = new StringBuilder();
		if (types != null) {
			for (int i = 0; i < types.length; ++i) {
				if (i > 0)
					res.append("|");
				res.append(types[i].toString());
			}
		}
		return res.toString();
	}

	private void increaseIndent() {
		for (int i = 0; i < INDENT_WIDTH; ++i) {
			prefix.append(' ');
		}
	}

	private void newLine(StringBuilder sb) {
		if (indentation) {
			sb.append(Constants.LINEBREAK).append(prefix);
		}
	}

	private void decreaseIndent() {
		prefix.setLength(Math.max(0, prefix.length() - INDENT_WIDTH));
	}
}
