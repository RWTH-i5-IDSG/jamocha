package org.jamocha.parser.clips;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jamocha.parser.Expression;
import org.jamocha.parser.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;

public class CLIPSFormatter implements Formatter {

	public String formatExpression(Expression expression) {
		if (expression instanceof JamochaValue) {
			return formatJamochaValue((JamochaValue) expression);
		} else if (expression instanceof FunctionParam2) {
			return formatFunctionParam((FunctionParam2) expression);
		} else if (expression instanceof BoundParam) {
			return formatBoundParam((BoundParam)expression);
		} else if (expression instanceof SlotParam) {
			return formatSlotParam((SlotParam)expression);
		} else if (expression instanceof ExpressionCollection) {
			return formatExpressionCollection((ExpressionCollection)expression);
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
}
