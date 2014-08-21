package org.jamocha.logging.formatter;

import java.text.StringCharacterIterator;

import org.jamocha.filter.SymbolCollector;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.RuleCondition;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author "Christoph Terwelp <christoph.terwelp@rwth-aachen.de>"
 */
public class RuleConditionFormatter implements Formatter<RuleCondition> {

	private static final RuleConditionFormatter singleton = new RuleConditionFormatter();

	static public RuleConditionFormatter getRuleConditionFormatter() {
		return singleton;
	}

	static public String formatRC(RuleCondition re) {
		return getRuleConditionFormatter().format(re);
	}

	static private void lineBreak(StringBuffer output, int level) {
		output.append("\n");
		for (int i = 0; i < level; i++) {
			output.append("   ");
		}
	}

	static private void processStatement(StringCharacterIterator iter, StringBuffer output,
			int level) {
		if (iter.current() != '(')
			throw new Error("Expected '(' but found '" + iter.current() + "'");
		output.append('(');
		boolean lb = false;
		boolean sub = false;
		while (iter.next() != StringCharacterIterator.DONE) {
			switch (iter.current()) {
			case '(':
				if (!lb)
					lineBreak(output, level + 1);
				processStatement(iter, output, level + 1);
				sub = true;
				lb = false;
				break;
			case ')':
				if (!lb && sub)
					lineBreak(output, level);
				output.append(')');
				return;
			default:
				output.append(iter.current());
				lb = false;
			}
		}
	}

	static public String formatRCindented(RuleCondition re) {
		String input = formatRC(re);
		StringBuffer output = new StringBuffer();
		StringCharacterIterator iterator = new StringCharacterIterator(input);
		processStatement(iterator, output, 0);
		return output.toString();
	}

	private RuleConditionFormatter() {
	}

	@Override
	public String format(RuleCondition re) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(SymbolCollector.newHashSet().collect(re)
						.toSlotVariablesByFactVariable());
		for (ConditionalElement ce : re.getConditionalElements()) {
			sb.append(" ");
			sb.append(cef.format(ce));
		}
		sb.append(")");
		return sb.toString();
	}
}
