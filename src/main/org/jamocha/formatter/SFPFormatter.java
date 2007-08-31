package org.jamocha.formatter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.ExpressionCollection;
import org.jamocha.rete.Fact;
import org.jamocha.rete.MultiSlot;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Slot;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.DeclarationConfiguration;
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
import org.jamocha.rete.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.rete.nodes.joinfilter.RightFieldAddress;
import org.jamocha.rule.Action;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.NotCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

public class SFPFormatter extends Formatter {

	@Override
	public String visit(Function object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(object.getName());
		sb.append(')');
		return sb.toString();
	}

	@Override
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
			GregorianCalendar c = object.getDateValue();
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
		case FACT:
		case LONG:
		case DOUBLE:
		case BOOLEAN:
		default:
			Object obj = object.getObjectValue();
			if (obj instanceof Formattable) {
				sb.append(((Formattable) obj).format(this));
			} else {
				sb.append(obj.toString());
			}
			break;
		}
		return sb.toString();
	}

	@Override
	public String visit(Signature object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(object.getSignatureName());
		Parameter[] params = object.getParameters();
		for (Parameter param : params) {
			sb.append(' ');
			sb.append(param.format(this));
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String visit(SlotConfiguration object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(object.getSlotName());
		Parameter[] params = object.getSlotValues();
		increaseIndent();
		for (Parameter param : params) {
			sb.append(' ');
			sb.append(param.format(this));
		}
		decreaseIndent();
		sb.append(')');
		return sb.toString();
	}

	@Override
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
		sb.append('(');
		Parameter[] params = object.getParams();
		for (int i = 0; i < params.length; ++i) {
			if (i > 0)
				sb.append(' ');
			sb.append(params[i].format(this));
		}
		sb.append(')');
		if (object.getActions().size() > 0) {
			newLine(sb);
			sb.append(object.getActions().format(this));
		}
		newLine(sb);
		return sb.toString();
	}

	@Override
	public String visit(BoundParam object) {
		return "?" + object.getVariableName();
	}

	@Override
	public String visit(ExpressionCollection object) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < object.size(); ++i) {
			if (i > 0)
				sb.append(' ');
			sb.append(object.get(i).format(this));
		}
		return sb.toString();
	}

	@Override
	public String visit(AssertConfiguration object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(object.getTemplateName());
		SlotConfiguration[] slots = object.getSlots();
		increaseIndent();
		for (SlotConfiguration slot : slots) {
			newLine(sb);
			sb.append('(');
			sb.append(slot.getSlotName());
			sb.append(' ');
			Parameter[] slotValues = slot.getSlotValues();
			for (int i = 0; i < slotValues.length; ++i) {
				if (i > 0)
					sb.append(' ');
				sb.append(slotValues[i].format(this));
			}
			sb.append(')');

		}
		decreaseIndent();
		newLine(sb);
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String visit(DefmoduleConfiguration object) {
		return object.getModuleName();
	}

	@Override
	public String visit(DefruleConfiguration object) {
		StringBuilder sb = new StringBuilder();
		sb.append(object.getRuleName());
		increaseIndent();
		if (object.getRuleDescription()!= null) {
			newLine(sb);
			sb.append("\"").append(object.getRuleDescription()).append("\"");
		}
		if (object.getDeclarationConfiguration() != null) {
			newLine(sb);
			sb.append(object.getDeclarationConfiguration().format(this));
		}
		newLine(sb);
		Condition[] conditions = object.getConditions();
		for (Condition condition : conditions) {
			sb.append(condition.format(this));
			newLine(sb);
		}
		sb.append("=>");
		newLine(sb);
		sb.append(object.getActions().format(this));
		decreaseIndent();
		newLine(sb);
		return sb.toString();
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
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		sb.append(object.getFactBinding().getVariableName());
		SlotConfiguration[] scArray = object.getSlots();
		for (SlotConfiguration sc : scArray) {
			sb.append(" ");
			sb.append(sc.format(this));
		}
		return sb.toString();
	}

	@Override
	public String visit(WhileDoConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(Rule object) {
		StringBuilder buf = new StringBuilder();
		buf.append("(defrule ").append(object.getModule().getModuleName());
		buf.append("::").append(object.getName());
		// now print out the rule properties
		increaseIndent();
		newLine(buf);
		buf.append("(declare ");
		increaseIndent();
		newLine(buf);
		buf.append("(salience ").append(object.getSalience()).append(") ");
		newLine(buf);
		buf.append("(rule-version ").append(object.getVersion()).append(") ");
		newLine(buf);
		buf.append("(remember-match ").append(object.getRememberMatch())
				.append(") ");
		newLine(buf);
		buf.append("(effective-date ").append(object.getEffectiveDate())
				.append(") ");
		newLine(buf);
		buf.append("(expiration-date ").append(object.getExpirationDate())
				.append(") ");
		decreaseIndent();
		newLine(buf);
		buf.append(") ");
		newLine(buf);
		Condition[] conditions = object.getConditions();
		for (int idx = 0; idx < conditions.length; idx++) {
			Condition c = conditions[idx];
			buf.append(c.format(this));
			if (idx == conditions.length - 1) {
				decreaseIndent();
			}
			newLine(buf);
		}
		buf.append("=>");
		increaseIndent();
		newLine(buf);
		// now append the actions
		Action[] actions = object.getActions();
		for (int idx = 0; idx < actions.length; idx++) {
			buf.append(actions[idx].format(this));
			if (idx == actions.length - 1) {
				decreaseIndent();
			}
			newLine(buf);
		}
		buf.append(')');
		return buf.toString();
	}

	@Override
	public String visit(ObjectCondition object) {
		StringBuilder sb = new StringBuilder();
		List<Constraint> constraints = object.getConstraints();
		List<Constraint> remainingConstraints = new LinkedList<Constraint>(
				constraints);
		for (Constraint constraint : constraints) {
			if (constraint instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint) constraint;
				if (bc.getIsObjectBinding()
						&& bc.getName().equals(object.getTemplateName())) {
					sb.append("?").append(bc.getVariableName());
					sb.append(" <- ");
					remainingConstraints.remove(constraint);
					break;
				}
			}
		}
		sb.append('(').append(object.getTemplateName());
		increaseIndent();
		for (Constraint constraint : remainingConstraints) {
			newLine(sb);
			sb.append(constraint.format(this));
		}
		decreaseIndent();
		newLine(sb);
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String visit(TestCondition object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(test ");
		sb.append(object.getFunction().format(this));
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String visit(BoundConstraint object) {
		StringBuilder sb = new StringBuilder();
		Slot slot = object.getSlot();
		if (slot != null){
		sb.append('(').append(slot.getName());
		sb.append(" ?").append(object.getVariableName());
		sb.append(')');
		return sb.toString();}
		else
			return "UNDEFINED SLOT";
	}

	@Override
	public String visit(LiteralConstraint object) {
		StringBuilder sb = new StringBuilder();
		Slot slot = object.getSlot();
		if (slot != null){
		sb.append('(').append(slot.getName());
		sb.append(' ').append(object.getValue().format(this));
		sb.append(')');
		return sb.toString();}
		else
			return "UNDEFINED SLOT";
	}

	@Override
	public String visit(LeftFieldAddress object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(RightFieldAddress object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(FunctionAction object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(object.getFunctionName());
		Expression[] parameters = object.getParameters();
		for (int i = 0; i < parameters.length; ++i) {
			sb.append(' ');
			sb.append(parameters[i].format(this));
		}
		sb.append(')');
		newLine(sb);
		return sb.toString();
	}

	@Override
	public String visit(DeclarationConfiguration object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(NotCondition object) {
		StringBuilder sb = new StringBuilder();
		sb.append("(not");
		List<Condition> conditions = object.getNestedConditionalElement();
		for (Condition condition : conditions) {
			sb.append(' ');
			sb.append(condition.format(this));
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String visit(Fact object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(object.getTemplate().getName());
		Slot[] slots = object.getTemplate().getAllSlots();
		increaseIndent();
		for (int i = 0; i < slots.length; ++i) {
			newLine(sb);
			sb.append('(').append(slots[i].getName()).append(' ');
			try {
				sb.append(object.getSlotValue(i).format(this));
			} catch (EvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append(")");
		}
		decreaseIndent();
		newLine(sb);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(Deftemplate object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(object.getName()).append(' ');
		Slot[] slots = object.getAllSlots();
		increaseIndent();
		for (int idx = 0; idx < slots.length; idx++) {
			newLine(sb);
			sb.append(slots[idx].format(this));
		}
		decreaseIndent();
		newLine(sb);
		if (object.getClassName() != null) {
			sb.append("[" + object.getClassName() + "] ");
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(MultiSlot object) {
		return "ms";
	}

	@Override
	public String visit(Slot object) {
		return "s";
	}

	@Override
	public String visit(TemplateSlot object) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		if (object.isSilent())
			sb.append("silent ");
		if (object.isMultiSlot())
			sb.append("multislot ");
		else
			sb.append("slot ");
		sb.append(object.getName());
		increaseIndent();
		newLine(sb);
		sb.append("(type ").append(object.getValueType()).append(')');
		newLine(sb);
		sb.append("(default ");
		if (object.isRequired())
			sb.append("?NONE");
		else if (object.getDefaultExpression() != null)
			sb.append(object.getDefaultExpression().format(this));
		else
			sb.append("NIL");
		sb.append(')');
		decreaseIndent();
		newLine(sb);
		sb.append(')');
		return sb.toString();
	}
}
// (deffunction wurst "does nothing" (functiongroup miau) (?x ?y) (printout t ?x
// ?y))
