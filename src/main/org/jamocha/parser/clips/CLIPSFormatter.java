package org.jamocha.parser.clips;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;
import org.jamocha.rete.functions.ShellFunction;
import org.jamocha.rule.Action;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.MultiValue;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrLiteralConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

public class CLIPSFormatter implements Formatter {

	public CLIPSFormatter(boolean indentation) {

	}

	public String formatExpression(Expression expression) {
		if (expression instanceof JamochaValue) {
			return formatJamochaValue((JamochaValue) expression);
		} else if (expression instanceof FunctionParam2) {
			return formatFunctionParam((FunctionParam2) expression);
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
			sb.append(formatExpression(expressionCollection.get(i)));
		}
		return sb.toString();
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

	private String formatBoundParam(BoundParam boundParam) {
		if (boundParam.isMultislot()) {
			return "$?" + boundParam.getVariableName();
		} else {
			return "?" + boundParam.getVariableName();
		}
	}

	private String formatFunctionParam(FunctionParam2 funcParam) {
		if (funcParam.getFunctionName().equalsIgnoreCase("deffunction"))
			return formatFunctionParamDeffunction(funcParam);
		else
			return formatFunctionParamDefault(funcParam);
	}

	private String formatFunctionParamDeffunction(FunctionParam2 funcParam) {
		StringBuilder res = new StringBuilder("(" + funcParam.getFunctionName());
		Parameter[] params = funcParam.getParameters();
		res.append(" " + formatExpression(params[0]));
		res.append(" (" + formatExpression(params[1]) + ")");
		if (params != null) {
			for (Parameter param : params) {
				res.append(" " + formatExpression(param));
			}
		}
		res.append(")");
		return res.toString();
	}

	private String formatFunctionParamDefault(FunctionParam2 funcParam) {
		StringBuilder res = new StringBuilder("(" + funcParam.getFunctionName());
		Parameter[] params = funcParam.getParameters();
		if (params != null) {
			for (Parameter param : params) {
				res.append(" " + formatExpression(param));
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

	public String formatFunction(Function function) {
		// TODO Auto-generated method stub
		return null;
	}

	public String formatRule(Rule rule) {
		StringBuffer buf = new StringBuffer();
		buf.append("(defrule " + rule.getName() + Constants.LINEBREAK);
		// now print out the rule properties
		buf.append("  (declare (salience " + rule.getSalience()
				+ ") (rule-version " + rule.getVersion() + ") (remember-match "
				+ rule.getRememberMatch() + ") (effective-date "
				+ rule.getEffectiveDate() + ") (expiration-date "
				+ rule.getExpirationDate() + ") )" + Constants.LINEBREAK);
		Condition[] conditions = rule.getConditions();
		for (int idx = 0; idx < conditions.length; idx++) {
			Condition c = conditions[idx];
			buf.append(formatCondition(c));
		}
		buf.append("=>" + Constants.LINEBREAK);
		// now append the actions
		Action[] actions = rule.getActions();
		for (int idx = 0; idx < actions.length; idx++) {
			buf.append(formatAction(actions[idx]));
		}
		buf.append(")" + Constants.LINEBREAK);
		return buf.toString();
	}

	private StringBuffer formatAction(Action action) {
		// TODO Auto-generated method stub
		return null;
	}

	private String formatCondition(Condition condition) {
		if (condition instanceof TestCondition) {
			return formatTestCondition((TestCondition) condition);
		} else if (condition instanceof AndCondition) {
			return formatAndCondition((AndCondition) condition);
		} else if (condition instanceof ExistCondition) {
			return formatExistCondition((ExistCondition) condition);
		} else if (condition instanceof ObjectCondition) {
			return formatObjectCondition((ObjectCondition) condition);
		}
		return null;
	}

	private String formatObjectCondition(ObjectCondition condition) {
		StringBuffer buf = new StringBuffer();
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
		buf.append(pad).append(')').append(condition.getTemplateName()).append(
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
		StringBuffer buf = new StringBuffer();
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
		StringBuffer buf = new StringBuffer();
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

	private String formatAndCondition(AndCondition condition) {
		return "";
	}

	private String formatTestCondition(TestCondition condition) {
		StringBuffer buf = new StringBuffer();
		String pad = "  ";
		buf.append(pad).append('(').append(condition.getFunction().getName());
		if (condition.getFunction() instanceof ShellFunction) {
			Expression[] p = ((ShellFunction) condition.getFunction())
					.getParameters();
			for (int idx = 0; idx < p.length; idx++) {
				buf.append(' ').append(formatExpression(p[idx]));
			}
		}
		buf.append(')').append(Constants.LINEBREAK);
		return buf.toString();
	}
}
