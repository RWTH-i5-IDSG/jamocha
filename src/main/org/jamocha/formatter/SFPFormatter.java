package org.jamocha.formatter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Slot;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.DeffunctionConfiguration;
import org.jamocha.rete.configurations.DefmoduleConfiguration;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.configurations.IfElseConfiguration;
import org.jamocha.rete.configurations.LoopForCountConfiguration;
import org.jamocha.rete.configurations.ModifyConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.configurations.WhileDoConfiguration;
import org.jamocha.rete.functions.Function;

public class SFPFormatter extends Formatter {

	public String visit(Function object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(object.getName());
		sb.append(")");
		return sb.toString();
	}

	public String visit(JamochaValue object) {
		StringBuilder sb = new StringBuilder();
		switch (object.getType()) {
		case NIL:
			return "NIL";
		case STRING:
			return "\"" + object.getStringValue() + "\"";
		case FACT_ID:
			return "f-" + object.getFactIdValue();
		case DATETIME:
			GregorianCalendar c = (GregorianCalendar) object.getDateValue();
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
			break;
		case LIST:
			sb.append('[');
			for (int i = 0; i < object.getListCount(); ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(object.getListValue(i).format(this));
			}
			sb.append(']');
			break;
		case SLOT:
			sb.append('(');
			Slot slot = object.getSlotValue();
			sb.append(slot.getName());
			sb.append(' ');
			sb.append(slot.getValue().format(this));
			sb.append(')');
			break;

		default:
			sb.append(object.getObjectValue().toString());
			break;
		}
		return sb.toString();
	}

	public String visit(Signature object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(object.getSignatureName());
		Parameter[] params = object.getParameters();
		increaseIndent();
		for (Parameter param : params) {
			sb.append(" ");
			sb.append(param.format(this));
		}
		decreaseIndent();
		sb.append(")");
		return sb.toString();
	}

	public String visit(SlotConfiguration object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(object.getSlotName());
		Parameter[] params = object.getSlotValues();
		increaseIndent();
		for (Parameter param : params) {
			sb.append(" ");
			sb.append(param.format(this));
		}
		decreaseIndent();
		sb.append(")");
		return sb.toString();
	}

	public String visit(DeffunctionConfiguration object) {
		StringBuilder sb = new StringBuilder();
		sb.append(object.getFunctionName());
		if (object.getFunctionDescription() != null) {
			newLine(sb);
			sb.append("\"").append(object.getFunctionDescription())
					.append("\"");
		}
		if (object.definesFunctionGroup()) {
			newLine(sb);
			sb.append("(functiongroup \"").append(object.getFunctionGroup())
					.append("\")");
		}
		newLine(sb);
		sb.append("(");
		Parameter[] params = object.getParams();
		for (int i = 0; i < params.length; ++i) {
			if (i > 0)
				sb.append(" ");
			sb.append(params[i].format(this));
		}
		sb.append(")");
		if (object.getActions().size() > 0) {
			newLine(sb);
			sb.append(object.getActions().format(this));
		}
		newLine(sb);
		return sb.toString();
	}

	public String visit(BoundParam object) {
		return "?" + object.getVariableName();
	}

	public String visit(ExpressionSequence object) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < object.size(); ++i) {
			if (i > 0)
				newLine(sb);
			sb.append(object.get(i).format(this));
		}
		return sb.toString();
	}

	@Override
	public String visit(AssertConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(DefmoduleConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(DefruleConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(IfElseConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(LoopForCountConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ModifyConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(WhileDoConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}
}
// (deffunction wurst "does nothing" (functiongroup miau) (?x ?y) (printout t ?x
// ?y))
