package org.jamocha.parser.clips;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.Formatter;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.functions.FunctionDescription;
import org.jamocha.rule.Action;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BooleanOperatorCondition;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.MultiValue;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrLiteralConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

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
		if (expression instanceof JamochaValue) {
			return formatJamochaValue((JamochaValue) expression);
		} else if (expression instanceof Signature) {
			return formatFunctionParam((Signature) expression);
		} else if (expression instanceof BoundParam) {
			return formatBoundParam((BoundParam) expression);
		} else if (expression instanceof SlotParam) {
			return formatSlotParam((SlotParam) expression);
		} else if (expression instanceof ExpressionCollection) {
			return formatExpressionCollection((ExpressionCollection) expression);
		}
		return "";
	}

	private String formatExpressionCollection(
			ExpressionCollection expressionCollection) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < expressionCollection.size(); ++i) {
			if (i > 0)
				newLine(sb);
			sb.append(formatExpression(expressionCollection.get(i)));
		}
		return sb.toString();
	}

	private String formatBoundParam(BoundParam boundParam) {
		if (boundParam.isMultislot()) {
			return "$?" + boundParam.getVariableName();
		} else {
			return "?" + boundParam.getVariableName();
		}
	}

	private String formatFunctionParam(Signature funcParam) {
		if (funcParam.getSignatureName().equalsIgnoreCase("assert"))
			return formatFunctionParamAssert(funcParam);
		else if (funcParam.getSignatureName().equalsIgnoreCase("deffunction"))
			return formatFunctionParamDeffunction(funcParam);
		else if (funcParam.getSignatureName().equalsIgnoreCase("defrule"))
			return formatFunctionParamDefrule(funcParam);
		else if (funcParam.getSignatureName().equalsIgnoreCase("deftemplate"))
			return formatFunctionParamDeftemplate(funcParam);
		else
			return formatFunctionParamDefault(funcParam);
	}

	private String formatFunctionParamAssert(Signature funcParam) {
		StringBuilder res = new StringBuilder("(");
		res.append(funcParam.getSignatureName());
		res.append(" (");
		if (funcParam.getParameters()[0] instanceof AssertConfiguration) {
			AssertConfiguration assertConf = (AssertConfiguration) funcParam
					.getParameters()[0];
			res.append(assertConf.getTemplateName());
			SlotConfiguration[] slots = assertConf.getSlots();
			increaseIndent();
			for (SlotConfiguration slot : slots) {
				newLine(res);
				res.append("(");
				res.append(slot.getSlotName());
				res.append(" ");
				Parameter[] slotValues = slot.getSlotValues();
				for (int i = 0; i < slotValues.length; ++i) {
					if (i > 0)
						res.append(" ");
					res.append(formatExpression(slotValues[i]));
				}
				res.append(")");

			}
		} else {
			res.append(formatExpression(funcParam.getParameters()[0]));
			Object[] slots = (Object[]) ((JamochaValue) funcParam
					.getParameters()[1]).getObjectValue();
			increaseIndent();
			for (Object obj : slots) {
				Slot slot = (Slot) obj;
				newLine(res);
				res.append("(");
				res.append(slot.getName());
				res.append(" ");
				res.append(formatExpression(slot.getValue()));
				res.append(")");

			}
		}
		decreaseIndent();
		newLine(res);
		res.append(") )");
		return res.toString();
	}

	private String formatFunctionParamDeffunction(Signature funcParam) {
		StringBuilder res = new StringBuilder("(");
		res.append(funcParam.getSignatureName());
		Parameter[] params = funcParam.getParameters();
		res.append(" " + formatExpression(params[0]));
		Parameter[] defFuncParams = (Parameter[]) ((JamochaValue) params[1])
				.getObjectValue();
		res.append(" (");
		for (int i = 0; i < defFuncParams.length; ++i) {
			if (i > 0)
				res.append(" ");
			res.append(formatExpression(defFuncParams[i]));
		}
		res.append(")");
		res.append(" ");
		increaseIndent();
		newLine(res);
		res.append(formatExpression((ExpressionCollection) params[2]));
		decreaseIndent();
		newLine(res);
		res.append(")");
		return res.toString();
	}

	private String formatFunctionParamDefrule(Signature funcParam) {
		return formatRule((Rule) ((JamochaValue) funcParam.getParameters()[0])
				.getObjectValue());
	}

	private String formatFunctionParamDeftemplate(Signature funcParam) {
		StringBuilder res = new StringBuilder("(");
		res.append(funcParam.getSignatureName());
		Template template = (Template) ((JamochaValue) funcParam
				.getParameters()[0]).getObjectValue();
		res.append(" ");
		res.append(template.getName());
		TemplateSlot[] slots = template.getAllSlots();
		increaseIndent();
		for (TemplateSlot slot : slots) {
			newLine(res);
			res.append("(");
			if (slot.isMultiSlot()) {
				res.append("multislot ");
				res.append(slot.getName());
				res.append(")");
			} else {
				res.append("slot ");
				res.append(slot.getName());
				increaseIndent();
				newLine(res);
				res.append("(type ");
				res.append(slot.getValueType().toString());
				res.append(")");
				if (slot.getDefaultExpression() != null) {
					newLine(res);
					res.append("(default ");
					res.append(formatExpression(slot.getDefaultExpression()));
					res.append(")");
				}
				decreaseIndent();
				newLine(res);
				res.append(")");
			}
		}
		decreaseIndent();
		newLine(res);
		res.append(")");
		return res.toString();
	}

	private String formatFunctionParamDefault(Signature funcParam) {
		StringBuilder res = new StringBuilder("(");
		res.append(funcParam.getSignatureName());
		Parameter[] params = funcParam.getParameters();
		int lineLength = res.length();
		if (params != null) {
			for (Parameter param : params) {
				String exp = formatExpression(param);
				lineLength += exp.length();
				if (indentation && lineLength > 80) {
					newLine(res);
					lineLength = exp.length();
				}
				res.append(" " + exp);
			}
		}
		res.append(")");
		return res.toString();
	}

	private String formatJamochaValue(JamochaValue jamochaValue) {
		StringBuilder sb = new StringBuilder();
		switch (jamochaValue.getType()) {
		case NIL:
			return "NIL";
		case STRING:
			return "\"" + jamochaValue.getStringValue() + "\"";
		case FACT_ID:
			return "f-" + jamochaValue.getFactIdValue();
		case DATETIME:
			GregorianCalendar c = (GregorianCalendar) jamochaValue
					.getDateValue();
			sb.append('"');
			sb.append(fillToFixedLength(c.get(Calendar.YEAR), "0", 4)).append(
					'-');
			sb.append(fillToFixedLength(c.get(Calendar.MONTH) + 1, "0", 2))
					.append('-');
			sb.append(fillToFixedLength(c.get(Calendar.DAY_OF_MONTH), "0", 2))
					.append(' ');
			sb.append(fillToFixedLength(c.get(Calendar.HOUR_OF_DAY), "0", 2))
					.append(':');
			sb.append(fillToFixedLength(c.get(Calendar.MINUTE), "0", 2))
					.append(':');
			sb.append(fillToFixedLength(c.get(Calendar.SECOND), "0", 2));
			int gmtOffsetMillis = c.get(Calendar.ZONE_OFFSET);
			if (gmtOffsetMillis >= 0) {
				sb.append('+');
			} else {
				sb.append('-');
			}
			int gmtOffsetHours = gmtOffsetMillis / (1000 * 60 * 60);
			sb.append(fillToFixedLength(gmtOffsetHours, "0", 2));
			sb.append('"');
			break;
		case LIST:
			sb.append('[');
			for (int i = 0; i < jamochaValue.getListCount(); ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(formatJamochaValue(jamochaValue.getListValue(i)));
			}
			sb.append(']');
			break;
		case SLOT:
			sb.append('(');
			Slot slot = jamochaValue.getSlotValue();
			sb.append(slot.getName());
			sb.append(' ');
			sb.append(formatJamochaValue(slot.getValue()));
			sb.append(')');
			break;

		default:
			sb.append(jamochaValue.getObjectValue().toString());
			break;
		}
		return sb.toString();
	}

	private String fillToFixedLength(int val, String fill, int length) {
		String res = String.valueOf(val);
		while (res.length() < length)
			res = fill + res;
		return res;
	}

	private String formatSlotParam(SlotParam slotParam) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(slotParam.getName());
		sb.append(' ');
		sb.append(formatExpression(slotParam.getValueExpression()));
		sb.append(')');
		return sb.toString();
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
		return sb.toString();
	}

	public String formatRule(Rule rule) {
		StringBuilder buf = new StringBuilder();
		buf.append("(defrule ").append(rule.getName());
		// now print out the rule properties
		increaseIndent();
		newLine(buf);
		buf.append("(declare ");
		increaseIndent();
		newLine(buf);
		buf.append("(salience ").append(rule.getSalience()).append(") ");
		newLine(buf);
		buf.append("(rule-version ").append(rule.getVersion()).append(") ");
		newLine(buf);
		buf.append("(remember-match ").append(rule.getRememberMatch()).append(
				") ");
		newLine(buf);
		buf.append("(effective-date ").append(rule.getEffectiveDate()).append(
				") ");
		newLine(buf);
		buf.append("(expiration-date ").append(rule.getExpirationDate())
				.append(") ");
		decreaseIndent();
		newLine(buf);
		buf.append(") ");
		newLine(buf);
		Condition[] conditions = rule.getConditions();
		for (int idx = 0; idx < conditions.length; idx++) {
			Condition c = conditions[idx];
			buf.append(formatCondition(c));
			if (idx == conditions.length - 1) {
				decreaseIndent();
			}
			newLine(buf);
		}
		buf.append("=>");
		increaseIndent();
		newLine(buf);
		// now append the actions
		Action[] actions = rule.getActions();
		for (int idx = 0; idx < actions.length; idx++) {
			buf.append(formatAction(actions[idx]));
			if (idx == actions.length - 1) {
				decreaseIndent();
			}
			newLine(buf);
		}
		buf.append(")");
		return buf.toString();
	}

	private String formatAction(Action action) {
		if (action instanceof FunctionAction) {
			return formatFunctionAction((FunctionAction) action);
		}
		return "<Unknown action>";
	}

	private String formatFunctionAction(FunctionAction action) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append('(').append(action.getFunctionName());
		Expression[] parameters = action.getParameters();
		if (indentation) {
			sb.append(Constants.LINEBREAK);
		}
		for (int i = 0; i < parameters.length; ++i) {
			sb.append(prefix).append(formatExpression(parameters[i]));
			if (indentation) {
				sb.append(Constants.LINEBREAK);
			}
		}
		sb.append(')');
		if (indentation) {
			sb.append(Constants.LINEBREAK);
		}
		return sb.toString();
	}

	private String formatCondition(Condition condition) {
		if (condition instanceof TestCondition) {
			return formatTestCondition((TestCondition) condition);
		} else if (condition instanceof AndCondition) {
			return formatAndCondition((BooleanOperatorCondition) condition);
		} else if (condition instanceof ExistCondition) {
			return formatExistCondition((ExistCondition) condition);
		} else if (condition instanceof ObjectCondition) {
			return formatObjectCondition((ObjectCondition) condition);
		}
		return null;
	}

	private String formatObjectCondition(ObjectCondition condition) {
		StringBuilder buf = new StringBuilder();
		int start = 0;
		// this is a hack, but it keeps the code simple for spacing
		// default indent for CE is 2 spaces
		String pad = "  ";
		boolean obind = false;
		Constraint[] constraints = condition.getConstraints();
		Constraint cn = constraints[0];
		if (cn instanceof BoundConstraint) {
			BoundConstraint bc = (BoundConstraint) cn;
			if (bc.getIsObjectBinding()) {
				start = 1;
				buf.append(bc.toFactBindingPPString());
				// since the first Constraint is a fact binding we
				// change the padding to 1 space
				pad = " ";
				obind = true;
			}
		}
		if (condition.getNegated()) {
			buf.append(pad).append("(not").append(Constants.LINEBREAK);
			pad = "    ";
		}
		buf.append(pad).append('(').append(condition.getTemplateName()).append(
				Constants.LINEBREAK);
		for (int idx = start; idx < constraints.length; idx++) {
			Constraint cnstr = constraints[idx];
			if (condition.getNegated()) {
				buf.append("  ");
			}
			buf.append(formatConstraint(cnstr));
		}
		if (condition.getNegated()) {
			buf.append(pad).append(')').append(Constants.LINEBREAK);
			pad = "  ";
		}
		buf.append(pad);
		if (obind && !condition.getNegated()) {
			buf.append(' ');
		}
		buf.append(')').append(Constants.LINEBREAK);
		return buf.toString();
	}

	private String formatConstraint(Constraint constraint) {
		if (constraint instanceof AndLiteralConstraint) {
			return formatAndLiteralConstraint((AndLiteralConstraint) constraint);
		} else if (constraint instanceof BoundConstraint) {
			return formatBoundConstraint((BoundConstraint) constraint);
		} else if (constraint instanceof LiteralConstraint) {
			return formatLiteralConstraint((LiteralConstraint) constraint);
		} else if (constraint instanceof OrLiteralConstraint) {
			return formatOrLiteralConstraint((OrLiteralConstraint) constraint);
		} else if (constraint instanceof PredicateConstraint) {
			return formatPredicateConstraint((PredicateConstraint) constraint);
		}
		return "";
	}

	private String formatPredicateConstraint(PredicateConstraint constraint) {
		return "    (" + constraint.getName() + " "
				+ formatJamochaValue(constraint.getValue()) + ")"
				+ Constants.LINEBREAK;
	}

	private String formatOrLiteralConstraint(OrLiteralConstraint constraint) {
		StringBuilder buf = new StringBuilder();
		Iterator itr = ((List) constraint.getValue().getObjectValue())
				.iterator();
		buf.append("    (").append(constraint.getName()).append(' ');
		int count = 0;
		while (itr.hasNext()) {
			MultiValue mv = (MultiValue) itr.next();
			if (count > 0) {
				buf.append("|");
			}
			if (mv.getNegated()) {
				buf.append("~").append(
						ConversionUtils.formatSlot(mv.getValue()));
			} else {
				buf.append(ConversionUtils.formatSlot(mv.getValue()));
			}
			count++;
		}
		buf.append(")").append(Constants.LINEBREAK);
		return buf.toString();
	}

	private String formatLiteralConstraint(LiteralConstraint constraint) {
		StringBuilder sb = new StringBuilder();
		sb.append("    (").append(constraint.getName()).append(' ');
		if (constraint.getNegated()) {
			sb.append('~');
		}
		sb.append(formatJamochaValue(constraint.getValue())).append(')')
				.append(Constants.LINEBREAK);
		return sb.toString();
	}

	private String formatBoundConstraint(BoundConstraint constraint) {
		StringBuilder sb = new StringBuilder();
		sb.append("    (").append(constraint.getName()).append(' ');
		if (constraint.isMultislot()) {
			sb.append('$');
		}
		sb.append('?').append(formatJamochaValue(constraint.getValue()))
				.append(')').append(Constants.LINEBREAK);
		return sb.toString();
	}

	private String formatAndLiteralConstraint(AndLiteralConstraint constraint) {
		StringBuilder buf = new StringBuilder();
		Iterator itr = ((List) constraint.getValue().getObjectValue())
				.iterator();
		buf.append("    (").append(constraint.getName()).append(" ");
		int count = 0;
		while (itr.hasNext()) {
			MultiValue mv = (MultiValue) itr.next();
			if (count > 0) {
				buf.append("&");
			}
			if (mv.getNegated()) {
				buf.append("~").append(
						ConversionUtils.formatSlot(mv.getValue()));
			} else {
				buf.append(ConversionUtils.formatSlot(mv.getValue()));
			}
			count++;
		}
		buf.append(")").append(Constants.LINEBREAK);
		return buf.toString();

	}

	private String formatExistCondition(ExistCondition condition) {
		return "";
	}

	private String formatAndCondition(BooleanOperatorCondition condition) {
		return "";
	}

	private String formatTestCondition(TestCondition condition) {
		StringBuilder buf = new StringBuilder();
		String pad = "  ";
		buf.append(pad).append('(').append("test");
		buf.append(' ').append(formatExpression(condition.getFunction()));
		buf.append(')').append(Constants.LINEBREAK);
		return buf.toString();
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
