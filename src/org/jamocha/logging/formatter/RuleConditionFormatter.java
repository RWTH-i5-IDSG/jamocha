package org.jamocha.logging.formatter;

import java.util.List;

import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author "Christoph Terwelp <christoph.terwelp@rwth-aachen.de>"
 *
 */
public class RuleConditionFormatter extends Formatter {
	
	private static final RuleConditionFormatter singleton = new RuleConditionFormatter();
	private final ConditionalElementFormatter cef = ConditionalElementFormatter.getConditionalElementFormatter();
	
	static public RuleConditionFormatter getRuleConditionFormatter() {
		return singleton;
	}
	
	private RuleConditionFormatter() {
	}
	
	private String formatSingleVariables(List<SingleFactVariable> singleFactVariables, List<SingleSlotVariable> singleSlotVariables, int level) {
		StringBuilder sb = new StringBuilder();
		singleFactVariables.forEach((factVariable) -> {
			indent(sb, level);
			sb.append("(");
			sb.append(factVariable.getTemplate().getName() + " ");
			sb.append(factVariable.getSymbol().toString());
			int length = sb.length();
			singleSlotVariables.forEach((slotVariable) -> {
				if (slotVariable.getFactVariable() == factVariable) {
					sb.append("\n");
					indent(sb, level + 1);
					sb.append("(");
					sb.append(slotVariable.getSlot().getSlotName(factVariable.getTemplate()) + " ");
					sb.append(slotVariable.getSymbol().toString() +  ")");
				}
			});
			if (length != sb.length()) {
				sb.append("\n");
				indent(sb, level);
			}
			sb.append(")\n");
		});
		return sb.toString();
	}
	
	public String format(RuleCondition re) {
		return this.format(re, 0);
	}

	public String format(RuleCondition re, int level) {
		StringBuilder sb = new StringBuilder();
		indent(sb, level);
		sb.append("(\n");
		sb.append(this.formatSingleVariables(re.getSingleFactVariables(), re.getSingleSlotVariables(), level + 1));
		re.getConditionalElements().forEach((ce) -> {
			sb.append(cef.format(ce, level + 1));
		});
		indent(sb, level);
		sb.append(")\n");
		return sb.toString();
	}
}
